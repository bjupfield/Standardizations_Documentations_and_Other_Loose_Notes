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

/**
 * A case label element that refers to an expression
 * @since 21
 */
public interface PatternCaseLabelTree extends CaseLabelTree {

    /**
     * The pattern for the case.
     *
     * @return the pattern
     */
    public PatternTree getPattern();

}
