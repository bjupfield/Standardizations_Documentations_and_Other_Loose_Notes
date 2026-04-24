/*
 * Copyright (c) 2018, 2021, Oracle and/or its affiliates. All rights reserved.
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
 */

package sun.jvm.hotspot.gc.x;

import sun.jvm.hotspot.debugger.Address;
import sun.jvm.hotspot.runtime.VM;

class XBarrier {
    private static boolean is_weak_good_or_null_fast_path(Address addr) {
        return XAddress.is_weak_good_or_null(addr);
    }

    private static Address weak_load_barrier_on_oop_slow_path(Address addr) {
        return XAddress.is_weak_good(addr) ? XAddress.good(addr) : relocate_or_remap(addr);
    }

    private static boolean during_relocate() {
        return XGlobals.XGlobalPhase() == XGlobals.XPhaseRelocate;
    }

    private static Address relocate(Address addr) {
        return zheap().relocate_object(addr);
    }

    private static XHeap zheap() {
        XCollectedHeap zCollectedHeap = (XCollectedHeap)VM.getVM().getUniverse().heap();
        return zCollectedHeap.heap();
    }

    private static Address remap(Address addr) {
        return zheap().remapObject(addr);
    }

    private static Address relocate_or_remap(Address addr) {
        return during_relocate() ? relocate(addr) : remap(addr);
    }

    static Address weak_barrier(Address o) {
        // Fast path
        if (is_weak_good_or_null_fast_path(o)) {
            // Return the good address instead of the weak good address
            // to ensure that the currently active heap view is used.
            return XAddress.good_or_null(o);
        }

        // Slow path
        return weak_load_barrier_on_oop_slow_path(o);
    }
}
