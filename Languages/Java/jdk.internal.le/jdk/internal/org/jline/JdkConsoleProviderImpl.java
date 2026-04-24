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

package jdk.internal.org.jline;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import jdk.internal.io.JdkConsole;
import jdk.internal.io.JdkConsoleProvider;
import jdk.internal.org.jline.reader.EndOfFileException;
import jdk.internal.org.jline.reader.LineReader;
import jdk.internal.org.jline.reader.LineReaderBuilder;
import jdk.internal.org.jline.terminal.Terminal;
import jdk.internal.org.jline.terminal.TerminalBuilder;

/**
 * JdkConsole/Provider implementations for jline
 */
public class JdkConsoleProviderImpl implements JdkConsoleProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public JdkConsole console(boolean isTTY, Charset charset) {
        try {
            Terminal terminal = TerminalBuilder.builder().encoding(charset)
                                               .exec(false).build();
            return new JdkConsoleImpl(terminal);
        } catch (IllegalStateException ise) {
            //cannot create a non-dumb, non-exec terminal,
            //use the standard Console:
            return null;
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    /**
     * An implementation of JdkConsole, which act as a delegate for the
     * public Console class.
     */
    private static class JdkConsoleImpl implements JdkConsole {
        @Override
        public PrintWriter writer() {
            return terminal.writer();
        }

        @Override
        public Reader reader() {
            return terminal.reader();
        }

        @Override
        public JdkConsole format(String fmt, Object ... args) {
            writer().format(fmt, args).flush();
            return this;
        }

        @Override
        public JdkConsole printf(String format, Object ... args) {
            return format(format, args);
        }

        @Override
        public String readLine(String fmt, Object ... args) {
            try {
                return jline.readLine(fmt.formatted(args));
            } catch (EndOfFileException eofe) {
                return null;
            }
        }

        @Override
        public String readLine() {
            return readLine("");
        }

        @Override
        public char[] readPassword(String fmt, Object ... args) {
            try {
                return jline.readLine(fmt.formatted(args), '\0').toCharArray();
            } catch (EndOfFileException eofe) {
                return null;
            }
        }

        @Override
        public char[] readPassword() {
            return readPassword("");
        }

        @Override
        public void flush() {
            terminal.flush();
        }

        @Override
        public Charset charset() {
            return terminal.encoding();
        }

        private final LineReader jline;
        private final Terminal terminal;

        public JdkConsoleImpl(Terminal terminal) {
            this.terminal = terminal;
            this.jline = LineReaderBuilder.builder().terminal(terminal).build();
        }
    }
}
