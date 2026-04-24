/*
 * Copyright (c) 1999, 2022, Oracle and/or its affiliates. All rights reserved.
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
 * Thrown to indicate an operation cannot be completed because
 * it is not valid for a native method.
 *
 * @author Gordon Hirsch
 * @since  1.3
 */
public non-sealed class NativeMethodException extends OpaqueFrameException {
    private static final long serialVersionUID = 3924951669039469992L;

    public NativeMethodException() {
        super();
    }

    public NativeMethodException(String message) {
        super(message);
    }
}
