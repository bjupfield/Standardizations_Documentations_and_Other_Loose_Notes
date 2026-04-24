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

import jdk.internal.classfile.*;
import jdk.internal.classfile.constantpool.ConstantPoolBuilder;
import jdk.internal.classfile.constantpool.Utf8Entry;

public final class ChainedClassBuilder
        implements ClassBuilder, Consumer<ClassElement> {
    private final ClassBuilder downstream;
    private final DirectClassBuilder terminal;
    private final Consumer<ClassElement> consumer;

    public ChainedClassBuilder(ClassBuilder downstream,
                               Consumer<ClassElement> consumer) {
        this.downstream = downstream;
        this.consumer = consumer;
        this.terminal = switch (downstream) {
            case ChainedClassBuilder cb -> cb.terminal;
            case DirectClassBuilder db -> db;
        };
    }

    @Override
    public ClassBuilder with(ClassElement element) {
        consumer.accept(element);
        return this;
    }

    @Override
    public Optional<ClassModel> original() {
        return terminal.original();
    }

    @Override
    public ClassBuilder withField(Utf8Entry name, Utf8Entry descriptor, Consumer<? super FieldBuilder> handler) {
        return downstream.with(new BufferedFieldBuilder(terminal.constantPool,
                                                        name, descriptor, null)
                                       .run(handler)
                                       .toModel());
    }

    @Override
    public ClassBuilder transformField(FieldModel field, FieldTransform transform) {
        BufferedFieldBuilder builder = new BufferedFieldBuilder(terminal.constantPool,
                                                                field.fieldName(), field.fieldType(),
                                                                field);
        builder.transform(field, transform);
        return downstream.with(builder.toModel());
    }

    @Override
    public ClassBuilder withMethod(Utf8Entry name, Utf8Entry descriptor, int flags,
                                   Consumer<? super MethodBuilder> handler) {
        return downstream.with(new BufferedMethodBuilder(terminal.constantPool,
                                                         name, descriptor, null)
                                       .run(handler)
                                       .toModel());
    }

    @Override
    public ClassBuilder transformMethod(MethodModel method, MethodTransform transform) {
        BufferedMethodBuilder builder = new BufferedMethodBuilder(terminal.constantPool,
                                                                  method.methodName(), method.methodType(), method);
        builder.transform(method, transform);
        return downstream.with(builder.toModel());
    }

    @Override
    public ConstantPoolBuilder constantPool() {
        return terminal.constantPool();
    }

}
