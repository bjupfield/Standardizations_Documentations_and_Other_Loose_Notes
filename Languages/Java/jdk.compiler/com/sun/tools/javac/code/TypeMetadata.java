/*
 * Copyright (c) 2014, 2023, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.tools.javac.code;

import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

/**
 * A type metadata is an object that can be stapled on a type. This is typically done using
 * {@link Type#addMetadata(TypeMetadata)}. Metadata associated to a type can also be removed,
 * typically using {@link Type#dropMetadata(Class)}. To drop <em>all</em> metadata from a given type,
 * the {@link Type#baseType()} method can also be used. This can be useful when comparing two
 * using reference equality (see also {@link Type#equalsIgnoreMetadata(Type)}).
 * <p>
 * There are no constraints on how a type metadata should be defined. Typically, a type
 * metadata will be defined as a small record, storing additional information (see {@link ConstantValue}).
 * In other cases, type metadata can be mutable and support complex state transitions
 * (see {@link Annotations}).
 * <p>
 * The only invariant the implementation requires is that there must be <em>one</em> metadata
 * of a given kind attached to a type, as this makes accessing and dropping metadata simpler.
 * If clients wish to store multiple metadata values that are logically related, they should
 * define a metadata type that collects such values in e.g. a list.
 */
public sealed interface TypeMetadata {

    /**
     * A type metadata object holding type annotations. This metadata needs to be mutable,
     * because type annotations are sometimes set in two steps. That is, a type can be created with
     * an empty set of annotations (e.g. during member enter). At some point later, the type
     * is then updated to contain the correct annotations. At this point we need to augment
     * the existing type (rather than creating a new one), as the type might already have been
     * saved inside other symbols.
     */
    record Annotations(ListBuffer<Attribute.TypeCompound> annotationBuffer) implements TypeMetadata {

        Annotations() {
            this(new ListBuffer<>());
        }

        Annotations(List<Attribute.TypeCompound> annotations) {
            this();
            annotationBuffer.appendList(annotations);
        }

        List<Attribute.TypeCompound> annotations() {
            return annotationBuffer.toList();
        }
    }

    /**
     * A type metadata holding a constant value. This can be used to describe constant types,
     * such as the type of a string literal, or that of a numeric constant.
     */
    record ConstantValue(Object value) implements TypeMetadata { }
}
