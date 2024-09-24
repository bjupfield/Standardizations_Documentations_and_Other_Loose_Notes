/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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
package sun.java2d.marlin;

/**
 * DPQS Sorter context
 */
final class DPQSSorterContext {

    static final boolean LOG_ALLOC = false;
    static final boolean CHECK_ALLOC = false && LOG_ALLOC;

    /**
     * Max capacity of the index array for tracking runs.
     */
    static final int MAX_RUN_CAPACITY = DualPivotQuicksort20191112Ext.MAX_RUN_CAPACITY;

    /* members */
    final int[] run;
    int[] auxA;
    int[] auxB;
    boolean runInit;

    DPQSSorterContext() {
        // preallocate max runs:
        if (LOG_ALLOC) {
            MarlinUtils.logInfo("alloc run: " + MAX_RUN_CAPACITY);
        }
        run = new int[MAX_RUN_CAPACITY];
    }

    void initBuffers(final int length, final int[] a, final int[] b) {
        auxA = a;
        if (CHECK_ALLOC && (a.length < length)) {
            if (LOG_ALLOC) {
                MarlinUtils.logInfo("alloc auxA: " + length);
            }
            auxA = new int[length];
        }
        auxB = b;
        if (CHECK_ALLOC && (b.length < length)) {
            if (LOG_ALLOC) {
                MarlinUtils.logInfo("alloc auxB: " + length);
            }
            auxB = new int[length];
        }
        runInit = true;
    }

}
