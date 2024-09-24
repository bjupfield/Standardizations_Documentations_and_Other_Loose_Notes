/*
 * Copyright (c) 2010, 2023, Oracle and/or its affiliates. All rights reserved.
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

package sun.net.sdp;

import java.io.IOException;
import java.io.FileDescriptor;

import jdk.internal.access.SharedSecrets;
import jdk.internal.access.JavaIOFileDescriptorAccess;
import jdk.internal.util.OperatingSystem;


/**
 * This class defines methods for creating SDP sockets or "converting" existing
 * file descriptors, referencing (unbound) TCP sockets, to SDP.
 */

public final class SdpSupport {
    private static final boolean isSupported = OperatingSystem.isLinux();
    private static final JavaIOFileDescriptorAccess fdAccess =
        SharedSecrets.getJavaIOFileDescriptorAccess();

    private SdpSupport() { }

    /**
     * Creates a SDP socket, returning file descriptor referencing the socket.
     */
    public static FileDescriptor createSocket() throws IOException {
        if (!isSupported)
            throw new UnsupportedOperationException("SDP not supported on this platform");
        int fdVal = create0();
        FileDescriptor fd = new FileDescriptor();
        fdAccess.set(fd, fdVal);
        return fd;
    }

    /**
     * Converts an existing file descriptor, that references an unbound TCP socket,
     * to SDP.
     */
    public static void convertSocket(FileDescriptor fd) throws IOException {
        if (!isSupported)
            throw new UnsupportedOperationException("SDP not supported on this platform");
        int fdVal = fdAccess.get(fd);
        convert0(fdVal);
    }

    private static native int create0() throws IOException;

    private static native void convert0(int fd) throws IOException;

    static {
        jdk.internal.loader.BootLoader.loadLibrary("net");
    }
}
