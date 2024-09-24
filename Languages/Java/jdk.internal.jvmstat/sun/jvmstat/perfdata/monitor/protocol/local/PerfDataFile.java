/*
 * Copyright (c) 2004, 2022, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvmstat.perfdata.monitor.protocol.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.FilenameFilter;
import sun.jvmstat.PlatformSupport;

/**
 * Class to provide translations from the local Vm Identifier
 * name space into the file system name space and vice-versa.
 * <p>
 * Provides a factory for creating a File object to the backing
 * store file for instrumentation shared memory region for a JVM
 * identified by its Local Java Virtual Machine Identifier, or
 * <em>lvmid</em>.
 *
 * @author Brian Doherty
 * @since 1.5
 * @see java.io.File
 */
public class PerfDataFile {
    private PerfDataFile() { };

    /**
     * The file name prefix for PerfData shared memory files.
     * <p>
     * This prefix must be kept in sync with the prefix used by the JVM.
     */
    public static final String dirNamePrefix = "hsperfdata_";

    /**
     * The directory name pattern for the user directories.
     */
    public static final String userDirNamePattern = "hsperfdata_\\S*";

    /**
     * The file name pattern for PerfData shared memory files.
     * <p>
     * This pattern must be kept in synch with the file name pattern
     * used by the 1.4.2 and later HotSpot JVM. Earlier versions are
     * no longer supported.
     */
    public static final String fileNamePattern = "^[0-9]+$";

    /**
     * Platform Specific methods for looking up temporary directories
     * and process IDs.
     */
    private static final PlatformSupport platSupport = PlatformSupport.getInstance();

    /**
     * Method to extract a local Java Virtual Machine Identifier from the
     * file name of the given File object.
     *
     * @param file A File object representing the name of a
     *             shared memory region for a target JVM
     * @return int - the local Java Virtual Machine Identifier for the target
     *               associated with the file
     * @throws java.lang.IllegalArgumentException Thrown if the file name
     *               does not conform to the expected pattern
     */
    public static int getLocalVmId(File file) {
        try {
            return(platSupport.getLocalVmId(file));
        } catch (NumberFormatException e) { }

        throw new IllegalArgumentException("Cannot convert '" + file + "' to VM id");
    }

    /**
     * Return the name of the temporary directory being searched for
     * HotSpot PerfData backing store files.
     * <p>
     * This method generally returns the value of the java.io.tmpdir
     * property. However, on some platforms it may return a different
     * directory, as the JVM implementation may store the PerfData backing
     * store files in a different directory for performance reasons.
     *
     * @return String - the name of the temporary directory.
     */
    public static String getTempDirectory() {
        return PlatformSupport.getTemporaryDirectory();
    }

    /**
     * Return the names of the temporary directories being searched for
     * HotSpot PerfData backing store files.
     * <p>
     * This method returns the traditional host temp directory but also
     * includes a list of temp directories used by containers.
     *
     * @return List<String> - A List of temporary directories to search.
     */
    public static List<String> getTempDirectories(int vmid) {
        return platSupport.getTemporaryDirectories(vmid);
    }
}
