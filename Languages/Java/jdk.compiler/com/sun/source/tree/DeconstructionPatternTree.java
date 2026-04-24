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

package com.sun.source.tree;

import java.util.List;

/**
 * A deconstruction pattern tree.
 *
 * @since 21
 */
public interface DeconstructionPatternTree extends PatternTree {

    /**
     * Returns the deconstructed type.
     * @return the deconstructed type
     */
    ExpressionTree getDeconstructor();

    /**
     * Returns the nested patterns.
     * @return the nested patterns.
     */
    List<? extends PatternTree> getNestedPatterns();

}

