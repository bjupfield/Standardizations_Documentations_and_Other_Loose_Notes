/*
 * Copyright (c) 2018, 2023, Oracle and/or its affiliates. All rights reserved.
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
package jdk.internal.org.jline.terminal.impl.jna;

@SuppressWarnings("serial")
public class LastErrorException extends RuntimeException{

    public final long lastError;

    public LastErrorException(long lastError) {
        this.lastError = lastError;
    }

}
