/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

/**
 * A tree node for a character represented by an escape sequence.
 *
 * @apiNote This class does not itself constrain the set of valid escape sequences,
 * although the set may be effectively constrained to those defined in the
 * <a href="{@docRoot}/../specs/javadoc/doc-comment-spec.html#escape-sequences">
 * Documentation Comment Specification for the Standard Doclet</a>,
 * including the following context-sensitive escape sequences:
 *
 * <ul>
 * <li>{@code @@}, representing {@code @}, where it would otherwise be treated as introducing a block or inline tag,
 * <li>{@code @/}, representing {@code /}, as part of {@code *@/} to represent <code>&ast;&sol;</code>, and
 * <li>{@code @*}, representing {@code *}, where it would otherwise be {@linkplain Elements#getDocComment(Element) discarded},
 *     after whitespace at the beginning of a line.
 * </ul>
 *
 * @since 21
 */
public interface EscapeTree extends TextTree {
    /**
     * {@inheritDoc}
     *
     * <p>Note: this method returns the escaped character, not the original escape sequence.
     */
    @Override
    String getBody();
}
