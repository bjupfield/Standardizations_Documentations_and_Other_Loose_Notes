/*
 * Copyright (c) 2020, 2021, Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;

/**
 * A tree node for an {@code @snippet} inline tag.
 *
 * <pre>
 *    {&#064;snippet :
 *     body
 *    }
 *
 *    {&#064;snippet attributes}
 *
 *    {&#064;snippet attributes :
 *     body
 *    }
 * </pre>
 *
 * @since 18
 */
public interface SnippetTree extends InlineTagTree {

    /**
     * Returns the list of the attributes of the {@code @snippet} tag.
     *
     * @return the list of the attributes
     */
    List<? extends DocTree> getAttributes();

    /**
     * Returns the body of the {@code @snippet} tag, or {@code null} if there is no body.
     *
     * @apiNote
     * An instance of {@code SnippetTree} with an empty body differs from an
     * instance of {@code SnippetTree} with no body.
     * If a tag has no body, then calling this method returns {@code null}.
     * If a tag has an empty body, then this method returns a {@code TextTree}
     * whose {@link TextTree#getBody()} returns an empty string.
     *
     * @return the body of the tag, or {@code null} if there is no body
     */
    TextTree getBody();
}
