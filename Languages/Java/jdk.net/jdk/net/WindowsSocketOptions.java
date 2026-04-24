/*
 * Copyright (c) 2022, 2023, Oracle and/or its affiliates. All rights reserved.
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
package jdk.net;

import java.net.SocketException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import jdk.net.ExtendedSocketOptions.PlatformSocketOptions;


@SuppressWarnings("removal")
class WindowsSocketOptions extends PlatformSocketOptions {

    public WindowsSocketOptions() {
    }

    @Override
    boolean ipDontFragmentSupported() {
        return true;
    }

    @Override
    boolean keepAliveOptionsSupported() {
        return keepAliveOptionsSupported0();
    }

    @Override
    void setIpDontFragment(int fd, final boolean value, boolean isIPv6) throws SocketException {
        setIpDontFragment0(fd, value, isIPv6);
    }

    @Override
    boolean getIpDontFragment(int fd, boolean isIPv6) throws SocketException {
        return getIpDontFragment0(fd, isIPv6);
    }

    @Override
    void setTcpKeepAliveProbes(int fd, final int value) throws SocketException {
        setTcpKeepAliveProbes0(fd, value);
    }

    @Override
    int getTcpKeepAliveProbes(int fd) throws SocketException {
        return getTcpKeepAliveProbes0(fd);
    }

    @Override
    void setTcpKeepAliveTime(int fd, final int value) throws SocketException {
        setTcpKeepAliveTime0(fd, value);
    }

    @Override
    int getTcpKeepAliveTime(int fd) throws SocketException {
        return getTcpKeepAliveTime0(fd);
    }

    @Override
    void setTcpKeepAliveIntvl(int fd, final int value) throws SocketException {
        setTcpKeepAliveIntvl0(fd, value);
    }

    @Override
    int getTcpKeepAliveIntvl(int fd) throws SocketException {
        return getTcpKeepAliveIntvl0(fd);
    }

    private static native boolean keepAliveOptionsSupported0();
    private static native void setIpDontFragment0(int fd, boolean value, boolean isIPv6) throws SocketException;
    private static native boolean getIpDontFragment0(int fd, boolean isIPv6) throws SocketException;
    private static native void setTcpKeepAliveProbes0(int fd, int value) throws SocketException;
    private static native int getTcpKeepAliveProbes0(int fd) throws SocketException;
    private static native void setTcpKeepAliveTime0(int fd, int value) throws SocketException;
    private static native int getTcpKeepAliveTime0(int fd) throws SocketException;
    private static native void setTcpKeepAliveIntvl0(int fd, int value) throws SocketException;
    private static native int getTcpKeepAliveIntvl0(int fd) throws SocketException;

    static {
        if (System.getSecurityManager() == null) {
            System.loadLibrary("extnet");
        } else {
            AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                System.loadLibrary("extnet");
                return null;
            });
        }
    }
}
