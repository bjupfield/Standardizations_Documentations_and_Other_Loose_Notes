/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package jdk.internal.classfile.impl;

import java.util.Optional;
import java.util.function.Consumer;

import jdk.internal.classfile.CodeBuilder;
import jdk.internal.classfile.CodeModel;
import jdk.internal.classfile.CodeTransform;
import jdk.internal.classfile.MethodBuilder;
import jdk.internal.classfile.MethodElement;
import jdk.internal.classfile.MethodModel;
import jdk.internal.classfile.constantpool.ConstantPoolBuilder;

public final class ChainedMethodBuilder implements MethodBuilder {
    final MethodBuilder downstream;
    final TerminalMethodBuilder terminal;
    final Consumer<MethodElement> consumer;

    public ChainedMethodBuilder(MethodBuilder downstream,
                                Consumer<MethodElement> consumer) {
        this.downstream = downstream;
        this.consumer = consumer;
        this.terminal = switch (downstream) {
            case ChainedMethodBuilder cb -> cb.terminal;
            case TerminalMethodBuilder tb -> tb;
        };
    }

    @Override
    public MethodBuilder with(MethodElement element) {
        consumer.accept(element);
        return this;
    }

    @Override
    public MethodBuilder withCode(Consumer<? super CodeBuilder> handler) {
        return downstream.with(terminal.bufferedCodeBuilder(null)
                                       .run(handler)
                                       .toModel());
    }

    @Override
    public MethodBuilder transformCode(CodeModel code, CodeTransform transform) {
        BufferedCodeBuilder builder = terminal.bufferedCodeBuilder(code);
        builder.transform(code, transform);
        return downstream.with(builder.toModel());
    }

    @Override
    public ConstantPoolBuilder constantPool() {
        return terminal.constantPool();
    }

    @Override
    public Optional<MethodModel> original() {
        return terminal.original();
    }

}
