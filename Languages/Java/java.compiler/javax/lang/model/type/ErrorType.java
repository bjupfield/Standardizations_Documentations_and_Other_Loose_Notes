/*
 * Copyright (c) 2005, 2022, Oracle and/or its affiliates. All rights reserved.
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

package javax.lang.model.type;

/**
 * Represents a class or interface type that cannot be properly modeled.
 * This may be the result of a processing error,
 * such as a missing class file or erroneous source code.
 * Most queries for
 * information derived from such a type (such as its members or its
 * supertype) will not, in general, return meaningful results.
 *
 * @since 1.6
 */
public interface ErrorType extends DeclaredType {
}
