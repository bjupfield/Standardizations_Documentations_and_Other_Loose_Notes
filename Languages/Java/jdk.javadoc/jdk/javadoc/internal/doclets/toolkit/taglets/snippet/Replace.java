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

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An action that replaces characters in text.
 */
public final class Replace implements Action {

    private final Pattern pattern;
    private final String replacement;
    private final StyledText text;

    /**
     * Constructs an action that replaces regex finds in text.
     *
     * @param replacement the replacement string
     * @param pattern the regex used to search the text
     * @param text the text
     */
    public Replace(String replacement, Pattern pattern, StyledText text) {
        this.replacement = replacement;
        this.pattern = pattern;
        this.text = text;
    }

    @Override
    public void perform() {
        record Replacement(int start, int end, String value) { }
        // until JDK-8261619 is resolved, translating replacements requires some
        // amount of waste and careful index manipulation
        String textString = text.asCharSequence().toString();
        Matcher matcher = pattern.matcher(textString);
        var replacements = new ArrayList<Replacement>();
        StringBuilder b = new StringBuilder();
        int off = 0; // cumulative offset caused by replacements (can become negative)
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            // replacements are computed as they may have special symbols
            matcher.appendReplacement(b, replacement);
            String s = b.substring(start + off);
            off = b.length() - end;
            replacements.add(new Replacement(start, end, s));
        }
        // there's no need to call matcher.appendTail(b)
        for (int i = replacements.size() - 1; i >= 0; i--) {
            Replacement r = replacements.get(i);
            text.subText(r.start(), r.end()).replace(Set.of(), r.value());
        }
    }
}
