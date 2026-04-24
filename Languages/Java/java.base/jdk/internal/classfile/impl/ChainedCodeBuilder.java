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

import jdk.internal.classfile.CodeBuilder;
import jdk.internal.classfile.CodeElement;
import jdk.internal.classfile.TypeKind;
import jdk.internal.classfile.Label;

import java.util.function.Consumer;

public final class ChainedCodeBuilder
        extends NonterminalCodeBuilder
        implements CodeBuilder {
    private final Consumer<CodeElement> consumer;

    public ChainedCodeBuilder(CodeBuilder downstream,
                              Consumer<CodeElement> consumer) {
        super(downstream);
        this.consumer = consumer;
    }

    @Override
    public Label startLabel() {
        return terminal.startLabel();
    }

    @Override
    public Label endLabel() {
        return terminal.endLabel();
    }

    @Override
    public int allocateLocal(TypeKind typeKind) {
        return parent.allocateLocal(typeKind);
    }

    @Override
    public CodeBuilder with(CodeElement element) {
        consumer.accept(element);
        return this;
    }
}
