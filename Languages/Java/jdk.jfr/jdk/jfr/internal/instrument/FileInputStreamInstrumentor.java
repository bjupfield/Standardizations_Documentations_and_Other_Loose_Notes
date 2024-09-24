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

import jdk.jfr.events.EventConfigurations;
import jdk.jfr.events.FileReadEvent;
import jdk.jfr.internal.event.EventConfiguration;

/**
 * See {@link JITracer} for an explanation of this code.
 */
@JIInstrumentationTarget("java.io.FileInputStream")
final class FileInputStreamInstrumentor {

    private FileInputStreamInstrumentor() {
    }

    private String path;

    @JIInstrumentationMethod
    public int read() throws IOException {
        EventConfiguration eventConfiguration = EventConfigurations.FILE_READ;
        if (!eventConfiguration.isEnabled()) {
            return read();
        }
        int result = 0;
        boolean endOfFile = false;
        long bytesRead = 0;
        long start = 0;
        try {
            start = EventConfiguration.timestamp();
            result = read();
            if (result < 0) {
                endOfFile = true;
            } else {
                bytesRead = 1;
            }
        } finally {
            long duration = EventConfiguration.timestamp() - start;
            if (eventConfiguration.shouldCommit(duration)) {
                FileReadEvent.commit(start, duration, path, bytesRead, endOfFile);
            }
        }
        return result;
    }

    @JIInstrumentationMethod
    public int read(byte b[]) throws IOException {
        EventConfiguration eventConfiguration = EventConfigurations.FILE_READ;
        if (!eventConfiguration.isEnabled()) {
            return read(b);
        }
        int bytesRead = 0;
        long start = 0;
        try {
            start = EventConfiguration.timestamp();
            bytesRead = read(b);
        } finally {
            long duration = EventConfiguration.timestamp() - start;
            if (eventConfiguration.shouldCommit(duration)) {
                if (bytesRead < 0) {
                    FileReadEvent.commit(start, duration, path, 0L, true);
                } else {
                    FileReadEvent.commit(start, duration, path, bytesRead, false);
                }
            }
        }
        return bytesRead;
    }

    @JIInstrumentationMethod
    public int read(byte b[], int off, int len) throws IOException {
        EventConfiguration eventConfiguration = EventConfigurations.FILE_READ;
        if (!eventConfiguration.isEnabled()) {
            return read(b, off, len);
        }
        int bytesRead = 0;
        long start = 0;
        try {
            start = EventConfiguration.timestamp();
            bytesRead = read(b, off, len);
        } finally {
            long duration = EventConfiguration.timestamp() - start;
            if (eventConfiguration.shouldCommit(duration)) {
                if (bytesRead < 0) {
                    FileReadEvent.commit(start, duration, path, 0L, true);
                } else {
                    FileReadEvent.commit(start, duration, path, bytesRead, false);
                }
            }
        }
        return bytesRead;
    }
}
