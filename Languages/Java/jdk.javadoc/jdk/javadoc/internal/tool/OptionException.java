/*
 * Copyright (c) 2016, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.tool;

import jdk.javadoc.internal.tool.Main.Result;

/**
 * Provides a general mechanism for the javadoc tool to indicate an option
 * decoding issue.
 */

class OptionException extends Exception {

    private static final long serialVersionUID = 0;

    public final Result result;
    public final String message;
    @SuppressWarnings("serial") // Type of field is not Serializable
    public final Runnable m;

    /**
     * Constructs an object with a result, runnable and a message
     * to be printed out by the catcher. The runnable can be invoked
     * by the catcher to display the usage text.
     * @param result the exit code
     * @param method the method to invoke
     * @param message the detailed message
     */
    public OptionException(Result result, Runnable method, String message) {
        this.result = result;
        this.m = method;
        this.message = message;
        if (result == null || result.isOK() || method == null || message == null) {
            throw new AssertionError("result == null || result.isOK() || " +
                    "method == null || message == null");
        }
    }

    /**
     * Constructs an object with a result and a runnable.
     * The runnable can be invoked by the catcher to display the usage text.
     * @param result the exit code
     * @param method the method to invoke
     */
    public OptionException(Result result, Runnable method) {
        this.result = result;
        this.m = method;
        this.message = null;
        if (result == null || method == null) {
            throw new AssertionError("result == null || method == null");
        }
    }
}
