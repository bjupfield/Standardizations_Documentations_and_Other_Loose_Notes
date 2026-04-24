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

/**
 * Facilities for declaring annotation processors and for
 * allowing annotation processors to communicate with an annotation processing
 * tool environment.
 *
 * <p> Unless otherwise specified in a particular implementation, the
 * collections returned by methods in this package should be expected
 * to be unmodifiable by the caller and unsafe for concurrent access.
 *
 * <p> Unless otherwise specified, methods in this package will throw
 * a {@code NullPointerException} if given a {@code null} argument.
 *
 * @since 1.6
 *
 * @see <a href="https://jcp.org/en/jsr/detail?id=269">
 * JSR 269: Pluggable Annotation Processing API</a>
 */
package javax.annotation.processing;
