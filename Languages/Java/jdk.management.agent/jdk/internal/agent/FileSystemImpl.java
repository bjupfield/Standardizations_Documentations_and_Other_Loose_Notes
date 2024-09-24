/*
 * Copyright (c) 2004, 2023, Oracle and/or its affiliates. All rights reserved.
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

package jdk.internal.agent;

import java.io.File;
import java.io.IOException;

/*
 * Windows implementation of sun.management.FileSystem
 */
@SuppressWarnings("removal")
public class FileSystemImpl extends FileSystem {

    public boolean supportsFileSecurity(File f) throws IOException {
        String path = f.getAbsolutePath();
        if (path.indexOf(0) >= 0) {
            throw new IOException("illegal filename");
        }
        return isSecuritySupported0(f.getAbsolutePath());
    }

    public boolean isAccessUserOnly(File f) throws IOException {
        String path = f.getAbsolutePath();
        if (path.indexOf(0) >= 0) {
            throw new IOException("illegal filename");
        }
        if (!isSecuritySupported0(path)) {
            throw new UnsupportedOperationException("File system does not support file security");
        }
        return isAccessUserOnly0(path);
    }

    // Native methods

    static native void init0();

    static native boolean isSecuritySupported0(String path) throws IOException;

    static native boolean isAccessUserOnly0(String path) throws IOException;

    // Initialization

    static {
        java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction<Void>() {
                public Void run() {
                    System.loadLibrary("management_agent");
                    return null;
                }
            });
        init0();
    }
}
