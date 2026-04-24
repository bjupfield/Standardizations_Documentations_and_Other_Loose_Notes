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

import jdk.jfr.events.FileWriteEvent;
import jdk.jfr.internal.event.EventConfiguration;
import jdk.jfr.events.EventConfigurations;

/**
 * See {@link JITracer} for an explanation of this code.
 */
@JIInstrumentationTarget("java.io.FileOutputStream")
final class FileOutputStreamInstrumentor {

    private FileOutputStreamInstrumentor() {
    }

    private String path;

    @JIInstrumentationMethod
    public void write(int b) throws IOException {
        EventConfiguration eventConfiguration = EventConfigurations.FILE_WRITE;
        if (!eventConfiguration.isEnabled()) {
            write(b);
            return;
        }
        long bytesWritten = 0;
        long start = 0;
        try {
            start = EventConfiguration.timestamp();
            write(b);
            bytesWritten = 1;
        } finally {
            long duration = EventConfiguration.timestamp() - start;
            if (eventConfiguration.shouldCommit(duration)) {
                FileWriteEvent.commit(start, duration, path, bytesWritten);
            }
        }
    }

    @JIInstrumentationMethod
    public void write(byte b[]) throws IOException {
        EventConfiguration eventConfiguration = EventConfigurations.FILE_WRITE;
        if (!eventConfiguration.isEnabled()) {
            write(b);
            return;
        }
        long bytesWritten = 0;
        long start = 0;
        try {
            start = EventConfiguration.timestamp();
            write(b);
            bytesWritten = b.length;
        } finally {
            long duration = EventConfiguration.timestamp() - start;
            if (eventConfiguration.shouldCommit(duration)) {
                FileWriteEvent.commit(start, duration, path, bytesWritten);
            }
        }
    }

    @JIInstrumentationMethod
    public void write(byte b[], int off, int len) throws IOException {
        EventConfiguration eventConfiguration = EventConfigurations.FILE_WRITE;
        if (!eventConfiguration.isEnabled()) {
            write(b, off, len);
            return;
        }
        long bytesWritten = 0;
        long start = 0;
        try {
            start = EventConfiguration.timestamp();
            write(b, off, len);
            bytesWritten = len;
        } finally {
            long duration = EventConfiguration.timestamp() - start;
            if (eventConfiguration.shouldCommit(duration)) {
                FileWriteEvent.commit(start, duration, path, bytesWritten);
            }
        }
    }
}
