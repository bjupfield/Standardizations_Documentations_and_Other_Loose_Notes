/*
 * Copyright (c) 2015, 2018, Oracle and/or its affiliates. All rights reserved.
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

package jdk.internal.jshell.tool;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import jdk.internal.org.jline.reader.UserInterruptException;

/**
 * Interface for defining user interaction with the shell.
 * @author Robert Field
 */
abstract class IOContext implements AutoCloseable {

    @Override
    public abstract void close() throws IOException;

    public abstract String readLine(String firstLinePrompt, String continuationPrompt, boolean firstLine, String prefix) throws IOException, InputInterruptedException;

    public abstract boolean interactiveOutput();

    public abstract Iterable<String> history(boolean currentSession);

    public abstract  boolean terminalEditorRunning();

    public abstract void suspend();

    public abstract void resume();

    public abstract void beforeUserCode();

    public abstract void afterUserCode();

    public abstract void replaceLastHistoryEntry(String source);

    public abstract int readUserInput() throws IOException;

    public char readUserInputChar() throws IOException {
        throw new UserInterruptException("");
    }

    public String readUserLine(String prompt) throws IOException {
        throw new UserInterruptException("");
    }

    public Writer userOutput() {
        throw new UnsupportedOperationException();
    }

    public char[] readPassword(String prompt) throws IOException {
        throw new UserInterruptException("");
    }

    public void setIndent(int indent) {}

    public Charset charset() {
        throw new UnsupportedOperationException();
    }

    class InputInterruptedException extends Exception {
        private static final long serialVersionUID = 1L;
    }
}

