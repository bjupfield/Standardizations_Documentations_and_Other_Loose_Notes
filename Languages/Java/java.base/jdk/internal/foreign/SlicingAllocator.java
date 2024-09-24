/*
 * Copyright (c) 2021, 2023, Oracle and/or its affiliates. All rights reserved.
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

package jdk.internal.foreign;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

public final class SlicingAllocator implements SegmentAllocator {

    private final MemorySegment segment;
    private final long maxAlign;

    private long sp = 0L;

    public SlicingAllocator(MemorySegment segment) {
        this.segment = segment;
        this.maxAlign = ((AbstractMemorySegmentImpl)segment).maxAlignMask();
    }

    MemorySegment trySlice(long byteSize, long byteAlignment) {
        long min = segment.address();
        long start = Utils.alignUp(min + sp, byteAlignment) - min;
        MemorySegment slice = segment.asSlice(start, byteSize, byteAlignment);
        sp = start + byteSize;
        return slice;
    }

    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {
        Utils.checkAllocationSizeAndAlign(byteSize, byteAlignment);
        // try to slice from current segment first...
        return trySlice(byteSize, byteAlignment);
    }
}
