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
import jdk.jfr.events.SocketWriteEvent;
import jdk.jfr.internal.event.EventConfiguration;

/**
 * See {@link JITracer} for an explanation of this code.
 */
@JIInstrumentationTarget("java.net.Socket$SocketOutputStream")
final class SocketOutputStreamInstrumentor {

    private SocketOutputStreamInstrumentor() {
    }

    @JIInstrumentationMethod
    public void write(byte b[], int off, int len) throws IOException {
        EventConfiguration eventConfiguration = EventConfigurations.SOCKET_WRITE;
        if (!eventConfiguration.isEnabled()) {
            write(b, off, len);
            return;
        }
        int bytesWritten = 0;
        long start = 0;
        try {
            start = EventConfiguration.timestamp();
            write(b, off, len);
            bytesWritten = len;
        } finally {
            long duration = EventConfiguration.timestamp() - start;
            if (eventConfiguration.shouldCommit(duration)) {
                InetAddress remote = parent.getInetAddress();
                SocketWriteEvent.commit(
                        start,
                        duration,
                        remote.getHostName(),
                        remote.getHostAddress(),
                        parent.getPort(),
                        bytesWritten);
            }
        }
    }

    // private field in java.net.Socket$SocketOutputStream
    private Socket parent;
}
