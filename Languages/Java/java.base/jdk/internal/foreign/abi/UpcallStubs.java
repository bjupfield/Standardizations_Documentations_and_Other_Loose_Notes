/*
 * Copyright (c) 2020, 2023, Oracle and/or its affiliates. All rights reserved.
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
package jdk.internal.foreign.abi;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.Arena;

import jdk.internal.foreign.MemorySessionImpl;

public final class UpcallStubs {

    private UpcallStubs() {
    }

    private static void freeUpcallStub(long stubAddress) {
        if (!freeUpcallStub0(stubAddress)) {
            throw new IllegalStateException("Not a stub address: " + stubAddress);
        }
    }

    // natives

    // returns true if the stub was found (and freed)
    private static native boolean freeUpcallStub0(long addr);

    private static native void registerNatives();
    static {
        registerNatives();
    }

    static MemorySegment makeUpcall(long entry, Arena arena) {
        MemorySessionImpl.toMemorySession(arena).addOrCleanupIfFail(new MemorySessionImpl.ResourceList.ResourceCleanup() {
            @Override
            public void cleanup() {
                freeUpcallStub(entry);
            }
        });
        return MemorySegment.ofAddress(entry).reinterpret(arena, null);
    }
}
