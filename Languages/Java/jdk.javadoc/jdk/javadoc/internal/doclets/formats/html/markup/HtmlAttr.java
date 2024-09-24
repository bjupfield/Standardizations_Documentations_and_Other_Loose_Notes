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

import jdk.javadoc.internal.doclets.toolkit.util.Utils;

/**
 * Enum representing HTML tag attributes.
 */
public enum HtmlAttr {
    ALT,
    ARIA_CONTROLS("aria-controls"),
    ARIA_EXPANDED("aria-expanded"),
    ARIA_LABEL("aria-label"),
    ARIA_LABELLEDBY("aria-labelledby"),
    ARIA_ORIENTATION("aria-orientation"),
    ARIA_SELECTED("aria-selected"),
    CHECKED,
    CLASS,
    CLEAR,
    COLS,
    CONTENT,
    DATA_COPIED("data-copied"), // custom HTML5 data attribute
    DISABLED,
    FOR,
    HREF,
    HTTP_EQUIV("http-equiv"),
    ID,
    LANG,
    NAME,
    ONCLICK,
    ONKEYDOWN,
    ONLOAD,
    PLACEHOLDER,
    REL,
    ROLE,
    ROWS,
    SCOPE,
    SCROLLING,
    SRC,
    STYLE,
    SUMMARY,
    TABINDEX,
    TARGET,
    TITLE,
    TYPE,
    VALUE,
    WIDTH;

    private final String value;

    public enum Role {

        BANNER,
        CONTENTINFO,
        MAIN,
        NAVIGATION,
        REGION;

        private final String role;

        Role() {
            role = Utils.toLowerCase(name());
        }

        public String toString() {
            return role;
        }
    }

    HtmlAttr() {
        this.value = Utils.toLowerCase(name());
    }

    HtmlAttr(String name) {
        this.value = name;
    }

    public String toString() {
        return value;
    }
}
