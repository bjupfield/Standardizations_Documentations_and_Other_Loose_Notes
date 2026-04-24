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
package jdk.internal.classfile;

import jdk.internal.classfile.impl.UnboundAttribute;

/**
 * Models a non-standard attribute of a classfile.  Clients should extend
 * this class to provide an implementation class for non-standard attributes,
 * and provide an {@link AttributeMapper} to mediate between the classfile
 * format and the {@linkplain CustomAttribute} representation.
 */
@SuppressWarnings("exports")
public abstract non-sealed class CustomAttribute<T extends CustomAttribute<T>>
        extends UnboundAttribute.CustomAttribute<T>
        implements CodeElement, ClassElement, MethodElement, FieldElement {

    /**
     * Construct a {@linkplain CustomAttribute}.
     * @param mapper the attribute mapper
     */
    protected CustomAttribute(AttributeMapper<T> mapper) {
        super(mapper);
    }
}
