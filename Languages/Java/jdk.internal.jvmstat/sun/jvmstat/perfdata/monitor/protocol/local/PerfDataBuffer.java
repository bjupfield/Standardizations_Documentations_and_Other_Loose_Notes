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

import jdk.internal.perf.Perf;
import sun.jvmstat.monitor.*;
import sun.jvmstat.perfdata.monitor.*;
import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.lang.reflect.Constructor;
import java.security.AccessController;

/**
 * The concrete PerfDataBuffer implementation for the <em>local:</em>
 * protocol for the HotSpot PerfData monitoring implementation.
 * <p>
 * This class is responsible for acquiring access to the shared memory
 * instrumentation buffer for the target HotSpot Java Virtual Machine.
 *
 * @author Brian Doherty
 * @since 1.5
 */
// Suppreess unchecked conversion warning at line 34.
//@SuppressWarnings("unchecked")
public class PerfDataBuffer extends AbstractPerfDataBuffer {
    @SuppressWarnings("removal")
    private static final Perf perf = AccessController.doPrivileged(new Perf.GetPerfAction());

    /**
     * Create a PerfDataBuffer instance for accessing the specified
     * instrumentation buffer.
     *
     * @param vmid the <em>local:</em> URI specifying the target JVM.
     *
     * @throws MonitorException
     */
    public PerfDataBuffer(VmIdentifier vmid) throws MonitorException {
        try {
            ByteBuffer bb = perf.attach(vmid.getLocalVmId());
            createPerfDataBuffer(bb, vmid.getLocalVmId());
        } catch (IOException | IllegalArgumentException e) {
            throw new MonitorException("Could not attach to "
                                       + vmid.getLocalVmId(), e);
        }
    }
}
