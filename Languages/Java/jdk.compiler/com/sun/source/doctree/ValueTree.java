/*
 * Copyright (c) 2011, 2022, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.source.doctree;

/**
 * A tree node for an {@code @value} inline tag.
 *
 * <pre>
 *    {&#064;value reference}
 *    {&#064;value format reference}
 * </pre>
 *
 * @since 1.8
 */
public interface ValueTree extends InlineTagTree {
    /**
     * Returns the reference to the value.
     * @return the reference
     */
    ReferenceTree getReference();

    /**
     * Returns the format string, or {@code null} if none was provided.
     *
     * @return the format string
     *
     * @implSpec This implementation returns {@code null}.
     * @since 20
     */
    default TextTree getFormat() {
        return null;
    }
}
