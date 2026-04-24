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

import java.util.*;
import java.util.regex.*;
import java.io.*;

/**
 * Class for managing the LocalMonitoredVm instances on the local system.
 * <p>
 * This class is responsible for the mechanism that detects the active
 * HotSpot Java Virtual Machines on the local host that can be accessed
 * by the current user. The ability to detect all possible HotSpot Java Virtual
 * Machines on the local host may be limited by the permissions of the
 * current user running this JVM.
 *
 * @author Brian Doherty
 * @since 1.5
 */
public class LocalVmManager {
    private FilenameFilter userDirFilter;
    private FilenameFilter userDirFileFilter;

    /**
     * Creates a LocalVmManager instance for the local system.
     * <p>
     * Manages LocalMonitoredVm instances for which the current user
     * has appropriate permissions.
     */
    public LocalVmManager() {
        // The files are in {tmpdir}/hsperfdata_{any_user_name}/[0-9]+
        Pattern userDirPattern = Pattern.compile(PerfDataFile.userDirNamePattern);
        userDirFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return userDirPattern.matcher(name).lookingAt();
            }
        };

        Pattern userDirFilePattern = Pattern.compile(PerfDataFile.fileNamePattern);
        userDirFileFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return userDirFilePattern.matcher(name).matches();
            }
        };
    }

    /**
     * Return the current set of monitorable Java Virtual Machines that
     * are accessible by the current user.
     *
     * @return Set - the Set of monitorable Java Virtual Machines
     */
    public synchronized Set<Integer> activeVms() {
        /*
         * TODO: this method was synchronized due to its thread-unsafe use of the regexp
         * Matcher objects. That is not the case anymore, but I am too afraid to change
         * it now. Maybe fix this later in a separate RFE.
         */
        Set<Integer> jvmSet = new HashSet<Integer>();
        List<String> tmpdirs = PerfDataFile.getTempDirectories(0);

        for (String dir : tmpdirs) {
            File tmpdir = new File(dir);
            if (! tmpdir.isDirectory()) {
                continue;
            }


            // Look for the files {tmpdir}/hsperfdata_{any_user_name}/[0-9]+
            // that are readable by the current user.
            File[] dirs = tmpdir.listFiles(userDirFilter);
            for (File subDir : dirs) {
                if (!subDir.isDirectory()) {
                    continue;
                }

                // get a list of files from the directory
                File[] files = subDir.listFiles(userDirFileFilter);
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.canRead()) {
                            int vmid = PerfDataFile.getLocalVmId(file);
                            if (vmid != -1) {
                                jvmSet.add(vmid);
                            }
                        }
                    }
                }
            }
        }
        return jvmSet;
    }
}
