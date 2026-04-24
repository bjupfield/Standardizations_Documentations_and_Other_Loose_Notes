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

package com.sun.source.tree;

import java.util.List;

import jdk.internal.javac.PreviewFeature;

/**
 * A tree node for a string template expression.
 *
 */
@PreviewFeature(feature=PreviewFeature.Feature.STRING_TEMPLATES, reflective=true)
public interface StringTemplateTree extends ExpressionTree {
    /**
     * Returns templated string processor (may be qualified) or null.
     *
     * @return templated string processor
     */
    ExpressionTree getProcessor();

    /**
     * Returns string fragments.
     *
     * @return string fragments
     */
    List<String> getFragments();

    /**
     * Returns list of expressions.
     *
     * @return list of expressions
     */
    List<? extends ExpressionTree> getExpressions();
}
