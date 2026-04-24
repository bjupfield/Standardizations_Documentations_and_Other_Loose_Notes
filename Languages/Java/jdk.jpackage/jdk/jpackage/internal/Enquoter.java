/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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
package jdk.jpackage.internal;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Add quotes to the given string in a configurable way.
 */
final class Enquoter {

    private Enquoter() {
        setQuoteChar('"');
    }

    static Enquoter forPropertyValues() {
        return new Enquoter()
                .setEnquotePredicate(QUOTE_IF_WHITESPACES)
                .setEscaper(PREPEND_BACKSLASH);
    }

    static Enquoter forShellLiterals() {
        return forShellLiterals('\'');
    }

    static Enquoter forShellLiterals(char quoteChar) {
        return new Enquoter()
                .setQuoteChar(quoteChar)
                .setEnquotePredicate(x -> true)
                .setEscaper(PREPEND_BACKSLASH);
    }

    String applyTo(String v) {
        if (!needQuotes.test(v)) {
            return v;
        } else {
            var buf = new StringBuilder();
            buf.appendCodePoint(beginQuoteChr);
            Optional.of(escaper).ifPresentOrElse(op -> {
                v.codePoints().forEachOrdered(chr -> {
                    if (chr == beginQuoteChr || chr == endQuoteChr) {
                        escaper.accept(chr, buf);
                    } else {
                        buf.appendCodePoint(chr);
                    }
                });
            }, () -> {
                buf.append(v);
            });
            buf.appendCodePoint(endQuoteChr);
            return buf.toString();
        }
    }

    Enquoter setQuoteChar(char chr) {
        beginQuoteChr = chr;
        endQuoteChr = chr;
        return this;
    }

    Enquoter setEscaper(BiConsumer<Integer, StringBuilder> v) {
        escaper = v;
        return this;
    }

    Enquoter setEnquotePredicate(Predicate<String> v) {
        needQuotes = v;
        return this;
    }

    private int beginQuoteChr;
    private int endQuoteChr;
    private BiConsumer<Integer, StringBuilder> escaper;
    private Predicate<String> needQuotes = str -> false;

    private final static Predicate<String> QUOTE_IF_WHITESPACES = new Predicate<String>() {
        @Override
        public boolean test(String t) {
            return pattern.matcher(t).find();
        }
        private final Pattern pattern = Pattern.compile("\\s");
    };

    private final static BiConsumer<Integer, StringBuilder> PREPEND_BACKSLASH = (chr, buf) -> {
        buf.append('\\');
        buf.appendCodePoint(chr);
    };
}
