/*
 * Copyright (c) 2013, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.jfr.internal.instrument;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import jdk.jfr.events.EventConfigurations;
import jdk.jfr.events.SocketReadEvent;
import jdk.jfr.internal.event.EventConfiguration;

/**
 * See {@link JITracer} for an explanation of this code.
 */
@JIInstrumentationTarget("java.net.Socket$SocketInputStream")
final class SocketInputStreamInstrumentor {

    private SocketInputStreamInstrumentor() {
    }

    @JIInstrumentationMethod
    public int read(byte b[], int off, int length) throws IOException {
        EventConfiguration eventConfiguration = EventConfigurations.SOCKET_READ;
        if (!eventConfiguration.isEnabled()) {
            return read(b, off, length);
        }
        int bytesRead = 0;
        long start = 0;
        try {
            start = EventConfiguration.timestamp();
            bytesRead = read(b, off, length);
        } finally {
            long duration = EventConfiguration.timestamp() - start;
            if (eventConfiguration.shouldCommit(duration)) {
                InetAddress remote = parent.getInetAddress();
                String host = remote.getHostName();
                String address = remote.getHostAddress();
                int port = parent.getPort();
                int timeout = parent.getSoTimeout();
                if (bytesRead < 0) {
                    SocketReadEvent.commit(start, duration, host, address, port, timeout, 0L, true);
                } else {
                    SocketReadEvent.commit(start, duration, host, address, port, timeout, bytesRead, false);
                }
            }
        }
        return bytesRead;
    }

    // private field in java.net.Socket$SocketInputStream
    private Socket parent;
}
