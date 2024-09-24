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

import jdk.internal.classfile.FieldBuilder;
import jdk.internal.classfile.FieldElement;
import jdk.internal.classfile.FieldModel;
import jdk.internal.classfile.constantpool.ConstantPoolBuilder;

public final class ChainedFieldBuilder implements FieldBuilder {
    private final TerminalFieldBuilder terminal;
    private final Consumer<FieldElement> consumer;

    public ChainedFieldBuilder(FieldBuilder downstream,
                               Consumer<FieldElement> consumer) {
        this.consumer = consumer;
        this.terminal = switch (downstream) {
            case ChainedFieldBuilder cb -> cb.terminal;
            case TerminalFieldBuilder tb -> tb;
        };
    }

    @Override
    public ConstantPoolBuilder constantPool() {
        return terminal.constantPool();
    }

    @Override
    public Optional<FieldModel> original() {
        return terminal.original();
    }

    @Override
    public FieldBuilder with(FieldElement element) {
        consumer.accept(element);
        return this;
    }

}

