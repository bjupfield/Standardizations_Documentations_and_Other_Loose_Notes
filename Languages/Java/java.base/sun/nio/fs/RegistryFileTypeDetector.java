/*
 * Copyright (c) 2008, 2022, Oracle and/or its affiliates. All rights reserved.
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

package sun.nio.fs;

import java.nio.file.Path;
import java.io.IOException;

/**
 * File type detector that does lookup of file extension using Windows Registry.
 */

public class RegistryFileTypeDetector
    extends AbstractFileTypeDetector
{
    public RegistryFileTypeDetector() {
        super();
    }

    @Override
    public String implProbeContentType(Path file) throws IOException {
        // get file extension
        Path name = file.getFileName();
        if (name == null)
            return null;
        String filename = name.toString();
        int dot = filename.lastIndexOf('.');
        if ((dot < 0) || (dot == (filename.length()-1)))
            return null;

        // query HKEY_CLASSES_ROOT\<ext>
        String key = filename.substring(dot);
        try (NativeBuffer keyBuffer = WindowsNativeDispatcher.asNativeBuffer(key);
             NativeBuffer nameBuffer = WindowsNativeDispatcher.asNativeBuffer("Content Type")) {
            return queryStringValue(keyBuffer.address(), nameBuffer.address());
        } catch (WindowsException we) {
            we.rethrowAsIOException(file.toString());
            return null; // keep compiler happy
        }
    }

    private static native String queryStringValue(long subKey, long name);

    static {
        // nio.dll has dependency on net.dll
        jdk.internal.loader.BootLoader.loadLibrary("net");
        jdk.internal.loader.BootLoader.loadLibrary("nio");
    }
}
