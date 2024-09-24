/*
 * Copyright (c) 2002, 2022, Oracle and/or its affiliates. All rights reserved.
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

package sun.nio.ch;


// Signalling operations on native threads

public class NativeThread {
    private static final long VIRTUAL_THREAD_ID = -1L;

    /**
     * Returns the id of the current native thread if the platform can signal
     * native threads, 0 if the platform can not signal native threads, or
     * -1L if the current thread is a virtual thread.
     */
    public static long current() {
        if (Thread.currentThread().isVirtual()) {
            return VIRTUAL_THREAD_ID;
        } else {
            // no support for signalling threads on Windows
            return 0;
        }
    }

    /**
     * Returns the id of the current native thread if the platform can signal
     * native threads, 0 if the platform can not signal native threads.
     */
    static long currentNativeThread() {
        return 0;
    }

    /**
     * Signals the given native thread.
     *
     * @throws IllegalArgumentException if tid is not a token to a native thread
     */
    static void signal(long tid) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns true the tid is the id of a native thread.
     */
    static boolean isNativeThread(long tid) {
        return false;
    }

    /**
     * Returns true if tid is -1L.
     * @see #current()
     */
    static boolean isVirtualThread(long tid) {
        return (tid == VIRTUAL_THREAD_ID);
    }
}
