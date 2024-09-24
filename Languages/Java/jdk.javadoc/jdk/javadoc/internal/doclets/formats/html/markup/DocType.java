/*
 * Copyright (c) 2010, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.formats.html.markup;

/**
 * Supported DOCTYPE declarations.
 */
public enum DocType {
    HTML5("<!DOCTYPE HTML>");

    public final String text;

    DocType(String text) {
        this.text = text;
    }
}
