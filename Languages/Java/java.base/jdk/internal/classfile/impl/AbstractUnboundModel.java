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

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jdk.internal.classfile.Attribute;
import jdk.internal.classfile.AttributedElement;
import jdk.internal.classfile.ClassfileElement;
import jdk.internal.classfile.CompoundElement;

public abstract sealed class AbstractUnboundModel<E extends ClassfileElement>
        extends AbstractElement
        implements CompoundElement<E>, AttributedElement
        permits BufferedCodeBuilder.Model, BufferedFieldBuilder.Model, BufferedMethodBuilder.Model {
    private final List<E> elements;
    private List<Attribute<?>> attributes;

    public AbstractUnboundModel(List<E> elements) {
        this.elements = elements;
    }

    @Override
    public void forEachElement(Consumer<E> consumer) {
        elements.forEach(consumer);
    }

    @Override
    public Stream<E> elementStream() {
        return elements.stream();
    }

    @Override
    public List<E> elementList() {
        return elements;
    }

    @Override
    public List<Attribute<?>> attributes() {
        if (attributes == null)
            attributes = elements.stream()
                                 .filter(e -> e instanceof Attribute)
                                 .<Attribute<?>>map(e -> (Attribute<?>) e)
                                 .toList();
        return attributes;
    }
}
