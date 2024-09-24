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

package jdk.javadoc.internal.doclets.toolkit.util;

import jdk.javadoc.internal.doclets.toolkit.DocletException;

/**
 * An unchecked exception that wraps a DocletException.
 * It can be used in places where a checked exception
 * is not permitted, such as in lambda expressions.
 */
public class UncheckedDocletException extends Error {
    private static final long serialVersionUID = -9131058909576418984L;

    public UncheckedDocletException(DocletException de) {
        super(de);
    }

    @Override
    public Throwable initCause(Throwable cause) {
        throw new UnsupportedOperationException();
    }
}
