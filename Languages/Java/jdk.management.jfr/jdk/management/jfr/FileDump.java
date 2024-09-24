/*
 * Copyright (c) 2021, 2022, Oracle and/or its affiliates. All rights reserved.
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
package jdk.management.jfr;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.Deque;

import jdk.management.jfr.DiskRepository.DiskChunk;

final class FileDump {
    private final Deque<DiskChunk> chunks = new ArrayDeque<>();
    private final long stopTimeMillis;
    private boolean complete;

    FileDump(long stopTimeMillis) {
        this.stopTimeMillis = stopTimeMillis;
    }

    public synchronized void add(DiskChunk dc) {
        if (isComplete()) {
            return;
        }
        dc.acquire();
        chunks.addFirst(dc);
        long endMillis = dc.endTimeNanos / 1_000_000;
        if (endMillis >= stopTimeMillis) {
            setComplete();
        }
    }

    public synchronized boolean isComplete() {
        return complete;
    }

    public synchronized void setComplete() {
        complete = true;
        this.notifyAll();
    }

    public synchronized void close() {
        for (DiskChunk dc : chunks) {
            dc.release();
        }
        chunks.clear();
        complete = true;
    }

    private DiskChunk oldestChunk() throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (!chunks.isEmpty()) {
                    return chunks.pollLast();
                }
                if (complete) {
                    return null;
                }
                this.wait();
            }
        }
    }

    public void write(Path path) throws IOException, InterruptedException {
        DiskChunk chunk = null;
        try (FileChannel out = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            while ((chunk = oldestChunk()) != null) {
                try (FileChannel in = FileChannel.open(chunk.path(), StandardOpenOption.READ)) {
                    in.transferTo(0, in.size(), out);
                }
                chunk.release();
                chunk = null;
            }
        } finally {
            if (chunk != null) {
                chunk.release();
            }
            close();
        }
    }
}