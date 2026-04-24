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

import jdk.internal.javac.PreviewFeature;

/**
 * A tree node for a binding pattern that matches a pattern
 * with a variable of any name and a type of the match candidate;
 * an unnamed pattern.
 *
 * For example the use of underscore {@code _} below:
 * <pre>
 *   if (r instanceof R(_)) {}
 * </pre>
 *
 * @jls 14.30.1 Kinds of Patterns
 *
 * @since 21
 */
@PreviewFeature(feature=PreviewFeature.Feature.UNNAMED)
public interface AnyPatternTree extends PatternTree {
}
