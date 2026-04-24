/*
 *  Copyright (c) 2019, 2022, Oracle and/or its affiliates. All rights reserved.
 *  ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 *
 */
package jdk.internal.foreign.layout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;
import java.util.List;
import java.util.Optional;

public final class StructLayoutImpl extends AbstractGroupLayout<StructLayoutImpl> implements StructLayout {

    private StructLayoutImpl(List<MemoryLayout> elements, long byteSize, long byteAlignment, long minByteAlignment, Optional<String> name) {
        super(Kind.STRUCT, elements, byteSize, byteAlignment, minByteAlignment, name);
    }

    @Override
    StructLayoutImpl dup(long byteAlignment, Optional<String> name) {
        return new StructLayoutImpl(memberLayouts(), byteSize(), byteAlignment, minByteAlignment, name);
    }

    public static StructLayout of(List<MemoryLayout> elements) {
        long size = 0;
        long align = 1;
        for (MemoryLayout elem : elements) {
            if (size % elem.byteAlignment() != 0) {
                throw new IllegalArgumentException("Invalid alignment constraint for member layout: " + elem);
            }
            size = Math.addExact(size, elem.byteSize());
            align = Math.max(align, elem.byteAlignment());
        }
        return new StructLayoutImpl(elements, size, align, align, Optional.empty());
    }

}
