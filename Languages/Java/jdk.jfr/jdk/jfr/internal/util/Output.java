/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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
package jdk.jfr.internal.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public interface Output {

    public void println();

    public void print(String s);

    public void print(String s, Object... args);

    default public void println(String s, Object... args) {
        print(s, args);
        println();
    }

    public void print(char c);

    public static final class LinePrinter implements Output {
        private final StringBuilder currentLine = new StringBuilder(80);
        private final List<String> lines = new ArrayList<>();

        @Override
        public void println() {
            lines.add(currentLine.toString());
            currentLine.setLength(0);
        }

        @Override
        public void print(String s) {
            currentLine.append(s);
        }

        @Override
        public void print(String s, Object... args) {
            currentLine.append(args.length > 0 ? String.format(s, args) : s);
        }

        @Override
        public void print(char c) {
            currentLine.append(c);
        }

        public List<String> getLines() {
            return lines;
        }
    }

    public static final class BufferedPrinter implements Output {
        private final StringBuilder buffer = new StringBuilder(100_000);
        private final PrintStream out;

        public BufferedPrinter(PrintStream out) {
            this.out = out;
        }

        @Override
        public void println() {
            buffer.append(System.lineSeparator());
            flushCheck();
        }

        @Override
        public void print(String s) {
            buffer.append(s);
            flushCheck();
        }

        @Override
        public void print(String s, Object... args) {
            if (args.length > 0) {
                buffer.append(String.format(s, args));
            } else {
                buffer.append(s);
            }
            flushCheck();
        }

        @Override
        public void print(char c) {
            buffer.append(c);
            flush();
        }

        public void flush() {
            out.print(buffer.toString());
            buffer.setLength(0);
        }

        private void flushCheck() {
            if (buffer.length() > 99_000) {
                flush();
            }
        }
    }
}