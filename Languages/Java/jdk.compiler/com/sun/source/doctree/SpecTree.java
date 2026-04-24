/*
 * Copyright (c) 2019, 2022, Oracle and/or its affiliates. All rights reserved.
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
 * A tree node for an {@code @spec} block tag.
 *
 * <pre>
 *    &#064;spec url title
 * </pre>
 *
 * @since 20
 */
public interface SpecTree extends BlockTagTree {
    /**
     * {@return the URL}
     */
    TextTree getURL();

    /**
     * {@return the title}
     */
    List<? extends DocTree> getTitle();
}
