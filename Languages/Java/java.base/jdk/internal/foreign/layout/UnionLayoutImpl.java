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
import java.lang.foreign.UnionLayout;
import java.util.List;
import java.util.Optional;

public final class UnionLayoutImpl extends AbstractGroupLayout<UnionLayoutImpl> implements UnionLayout {

    private UnionLayoutImpl(List<MemoryLayout> elements, long byteSize, long byteAlignment, long minByteAlignment, Optional<String> name) {
        super(Kind.UNION, elements, byteSize, byteAlignment, minByteAlignment, name);
    }

    @Override
    UnionLayoutImpl dup(long byteAlignment, Optional<String> name) {
        return new UnionLayoutImpl(memberLayouts(), byteSize(), byteAlignment, minByteAlignment, name);
    }

    public static UnionLayout of(List<MemoryLayout> elements) {
        long size = 0;
        long align = 1;
        for (MemoryLayout elem : elements) {
            size = Math.max(size, elem.byteSize());
            align = Math.max(align, elem.byteAlignment());
        }
        return new UnionLayoutImpl(elements, size, align, align, Optional.empty());
    }

}
