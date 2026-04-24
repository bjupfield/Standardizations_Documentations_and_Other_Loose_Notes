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
 * A case label element that refers to a constant expression
 * @since 21
 */
public interface ConstantCaseLabelTree extends CaseLabelTree {

    /**
     * The constant expression for the case.
     *
     * @return the constant expression
     */
    public ExpressionTree getConstantExpression();

}
