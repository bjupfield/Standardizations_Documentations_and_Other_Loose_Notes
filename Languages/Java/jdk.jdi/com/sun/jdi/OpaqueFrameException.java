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

package com.sun.jdi;

/**
 * Thrown to indicate an operation could not be performed on a frame.
 *
 * @since 19
 */
public sealed class OpaqueFrameException
        extends RuntimeException permits NativeMethodException {
    private static final long serialVersionUID = -6590097682282386695L;

    /**
     * Constructs a OpaqueFrameException with no detail message.
     */
    public OpaqueFrameException() {
        super();
    }

    /**
     * Constructs a OpaqueFrameException with the given detail message.
     *
     * @param message the detail message, can be {@code null}
     */
    public OpaqueFrameException(String message) {
        super(message);
    }
}
