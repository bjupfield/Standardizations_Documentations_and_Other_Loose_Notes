/*
 * Copyright (c) 2005, 2023, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.management;

import java.io.IOException;
import java.lang.management.PlatformManagedObject;

/**
 * Diagnostic management interface for the HotSpot Virtual Machine.
 *
 * <p>The diagnostic MBean is registered to the platform MBeanServer
 * as are other platform MBeans.
 *
 * <p>The {@code ObjectName} for uniquely identifying the diagnostic
 * MXBean within an MBeanServer is:
 * <blockquote>
 *    {@code com.sun.management:type=HotSpotDiagnostic}
 * </blockquote>
 *
 * It can be obtained by calling the
 * {@link PlatformManagedObject#getObjectName} method.
 *
 * All methods throw a {@code NullPointerException} if any input argument is
 * {@code null} unless it's stated otherwise.
 *
 * @see java.lang.management.ManagementFactory#getPlatformMXBeans(Class)
 */
public interface HotSpotDiagnosticMXBean extends PlatformManagedObject {
    /**
     * Dumps the heap to the {@code outputFile} file in the same
     * format as the hprof heap dump.
     * <p>
     * If this method is called remotely from another process,
     * the heap dump output is written to a file named {@code outputFile}
     * on the machine where the target VM is running.  If outputFile is
     * a relative path, it is relative to the working directory where
     * the target VM was started.
     *
     * @param  outputFile the system-dependent filename
     * @param  live if {@code true} dump only <i>live</i> objects
     *         i.e. objects that are reachable from others
     * @throws IOException if the {@code outputFile} already exists,
     *                     cannot be created, opened, or written to.
     * @throws UnsupportedOperationException if this operation is not supported.
     * @throws IllegalArgumentException if {@code outputFile} does not end with ".hprof" suffix.
     * @throws NullPointerException if {@code outputFile} is {@code null}.
     * @throws SecurityException
     *         If a security manager exists and its {@link
     *         java.lang.SecurityManager#checkWrite(java.lang.String)}
     *         method denies write access to the named file
     *         or the caller does not have ManagmentPermission("control").
     */
    public void dumpHeap(String outputFile, boolean live) throws IOException;

    /**
     * Returns a list of {@code VMOption} objects for all diagnostic options.
     * A diagnostic option is a {@link VMOption#isWriteable writeable}
     * VM option that can be set dynamically mainly for troubleshooting
     * and diagnosis.
     *
     * @return a list of {@code VMOption} objects for all diagnostic options.
     */
    public java.util.List<VMOption> getDiagnosticOptions();

    /**
     * Returns a {@code VMOption} object for a VM option of the given
     * name.
     *
     * @return a {@code VMOption} object for a VM option of the given name.
     * @throws NullPointerException if name is {@code null}.
     * @throws IllegalArgumentException if a VM option of the given name
     *                                     does not exist.
     */
    public VMOption getVMOption(String name);

    /**
     * Sets a VM option of the given name to the specified value.
     * The new value will be reflected in a new {@code VMOption}
     * object returned by the {@link #getVMOption} method or
     * the {@link #getDiagnosticOptions} method.  This method does
     * not change the value of this {@code VMOption} object.
     *
     * @param name Name of a VM option
     * @param value New value of the VM option to be set
     *
     * @throws IllegalArgumentException if the VM option of the given name
     *                                     does not exist.
     * @throws IllegalArgumentException if the new value is invalid.
     * @throws IllegalArgumentException if the VM option is not writable.
     * @throws NullPointerException if name or value is {@code null}.
     *
     * @throws  java.lang.SecurityException
     *     if a security manager exists and the caller does not have
     *     ManagementPermission("control").
     */
    public void setVMOption(String name, String value);

    /**
     * Generate a thread dump to the given file in the given format. The
     * {@code outputFile} parameter must be an absolute path to a file that
     * does not exist.
     *
     * <p> The thread dump will include output for all platform threads. It may
     * include output for some or all virtual threads.
     *
     * @implSpec
     * The default implementation throws {@code UnsupportedOperationException}.
     *
     * @apiNote
     * The output file is required to be an absolute path as the MXBean may be
     * accessed remotely from a tool or program with a different current working
     * directory.
     *
     * @param  outputFile the path to the file to create
     * @param  format the format to use
     * @throws IllegalArgumentException if the file path is not absolute
     * @throws IOException if the file already exists or an I/O exception is
     *         thrown writing to the file
     * @throws NullPointerException if either parameter is {@code null}
     * @throws SecurityException
     *         if a security manager is set and its {@link
     *         SecurityManager#checkWrite(java.lang.String)} method denies write
     *         access to the file or {@link java.lang.management.ManagementPermission
     *         ManagementPermission("control")} is denied
     * @throws UnsupportedOperationException if this operation is not supported
     * @since 21
     */
    default void dumpThreads(String outputFile, ThreadDumpFormat format) throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Thread dump format.
     * @since 21
     */
    public static enum ThreadDumpFormat {
        /**
         * Plain text format.
         */
        TEXT_PLAIN,
        /**
         * JSON (JavaScript Object Notation) format.
         */
        JSON,
    }
}
