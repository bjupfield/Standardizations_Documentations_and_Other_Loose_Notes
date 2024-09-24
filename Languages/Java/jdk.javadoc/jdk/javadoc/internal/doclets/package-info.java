/*
 * Copyright (c) 2003, 2022, Oracle and/or its affiliates. All rights reserved.
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
 * Doclets provide the user-selectable back ends for processing the
 * documentation comments in Java source code.
 *
 * <p>Doclets are implementations of the {@link jdk.javadoc.doclet Doclet API}.</p>
 *
 * <p>Currently, there is only one supported doclet, the
 * {@link jdk.javadoc.internal.doclets.formats.html.HtmlDoclet HtmlDoclet},
 * for writing API documentation in HTML. Nevertheless, in order to
 * separate the high-level code for the general content of each page
 * from the low-level details of how to write such content, the code is
 * organized in two sections: a format-neutral
 * {@link jdk.javadoc.internal.doclets.toolkit toolkit API},
 * and a specific {@link jdk.javadoc.internal.doclets.formats format},
 * such as {@link jdk.javadoc.internal.doclets.formats.html HTML format}.
 */
package jdk.javadoc.internal.doclets;
