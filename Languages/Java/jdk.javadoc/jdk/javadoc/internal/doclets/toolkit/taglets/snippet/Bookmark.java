/*
 * Copyright (c) 2020, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.toolkit.taglets.snippet;

/**
 * An action that associates text with a name.
 */
public final class Bookmark implements Action {

    private final String name;
    private final StyledText text;

    /**
     * Constructs an action that associates text with a name.
     *
     * @param name the string (key) to associate text with
     * @param text the text
     */
    public Bookmark(String name, StyledText text) {
        this.name = name;
        this.text = text;
    }

    @Override
    public void perform() {
        text.subText(0, text.length()).bookmark(name);
    }
}
