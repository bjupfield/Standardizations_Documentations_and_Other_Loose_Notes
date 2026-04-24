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

package com.sun.tools.javac.util;

/**
 * Exception thrown when invalid Modified UTF-8 is encountered.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 * @see Convert#utf2chars
 * @see Convert#utfValidate
 */
public class InvalidUtfException extends Exception {

    private static final long serialVersionUID = 0;

    private final int offset;

    public InvalidUtfException(int offset) {
        this.offset = offset;
    }

    /** Get the {@code byte[]} array offset at which the invalid data was found.
     */
    public int getOffset() {
        return offset;
    }
}
