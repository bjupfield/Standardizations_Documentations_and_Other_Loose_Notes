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

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An action that applies an additional style to text.
 */
public final class AddStyle implements Action {

    private final Style style;
    private final Pattern pattern;
    private final StyledText text;

    /**
     * Constructs an action that applies an additional style to regex finds in
     * text.
     *
     * @param style the style to add (to already existing styles)
     * @param pattern the regex used to search the text
     * @param text the text to search
     */
    public AddStyle(Style style, Pattern pattern, StyledText text) {
        this.style = style;
        this.pattern = pattern;
        this.text = text;
    }

    @Override
    public void perform() {
        var singleStyle = Set.of(style);
        Matcher matcher = pattern.matcher(text.asCharSequence());
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            text.subText(start, end).addStyle(singleStyle);
        }
    }
}
