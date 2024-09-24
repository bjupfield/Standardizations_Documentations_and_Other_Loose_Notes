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
package jdk.jshell;

import java.io.IOError;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * An interface providing functionality for {@link java.io.Console} in the user's snippet.
 * <p>
 * When a snippet calls a method on {@code Console}, the corresponding method in this interface will
 * be called.
 *
 * @since 21
 * @see java.io.Console
 */
public interface JShellConsole {

    /**
     * Retrieves the unique {@link java.io.PrintWriter PrintWriter} object
     * associated with this console.
     *
     * @return  The printwriter associated with this console
     * @see java.io.Console#writer()
     */
    public PrintWriter writer();

    /**
     * Retrieves the unique {@link java.io.Reader Reader} object associated
     * with this console.
     *
     * @return  The reader associated with this console
     * @see java.io.Console#reader()
     */
    public Reader reader();

    /**
     * Provides a prompt, then reads a single line of text from the
     * console.
     *
     * @param  prompt
     *         A prompt.
     *
     * @throws IOError
     *         If an I/O error occurs.
     *
     * @return  A string containing the line read from the console, not
     *          including any line-termination characters, or {@code null}
     *          if an end of stream has been reached.
     * @see java.io.Console#readLine()
     */
    public String readLine(String prompt) throws IOError;

    /**
     * Provides a prompt, then reads a password or passphrase from
     * the console with echoing disabled.
     *
     * @param  prompt
     *         A prompt.
     *
     * @throws IOError
     *         If an I/O error occurs.
     *
     * @return  A character array containing the password or passphrase read
     *          from the console, not including any line-termination characters,
     *          or {@code null} if an end of stream has been reached.
     * @see java.io.Console#readPassword()
     */
    public char[] readPassword(String prompt) throws IOError;

    /**
     * Flushes the console and forces any buffered output to be written
     * immediately.
     *
     * @see java.io.Console#flush()
     */
    public void flush();

    /**
     * Returns the {@link java.nio.charset.Charset Charset} object used for
     * the {@code Console}.
     *
     * @return a {@link java.nio.charset.Charset Charset} object used for the
     *          {@code Console}
     *
     * @see java.io.Console#charset()
     */
    public Charset charset();

}
