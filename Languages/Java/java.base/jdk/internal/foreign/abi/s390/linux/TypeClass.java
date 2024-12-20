/*
 * Copyright (c) 2022, 2023, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2023 IBM Corp. All rights reserved.
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
package jdk.internal.foreign.abi.s390.linux;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SequenceLayout;
import java.lang.foreign.ValueLayout;
import java.util.List;
import java.util.ArrayList;

public enum TypeClass {
    STRUCT_REGISTER,
    STRUCT_SFA, // Single Float Aggregate
    STRUCT_REFERENCE,
    POINTER,
    INTEGER,
    FLOAT;

    private static TypeClass classifyValueType(ValueLayout type) {
        Class<?> carrier = type.carrier();
        if (carrier == boolean.class || carrier == byte.class || carrier == char.class ||
                carrier == short.class || carrier == int.class || carrier == long.class) {
            return INTEGER;
        } else if (carrier == float.class || carrier == double.class) {
            return FLOAT;
        } else if (carrier == MemorySegment.class) {
            return POINTER;
        } else {
            throw new IllegalStateException("Cannot get here: " + carrier.getName());
        }
    }

    private static boolean isRegisterAggregate(MemoryLayout type) {
        long byteSize = type.byteSize();
        if (byteSize > 8 || byteSize == 3 || byteSize == 5 || byteSize == 6 || byteSize == 7)
            return false;
        return true;
    }

    static List<MemoryLayout> scalarLayouts(GroupLayout gl) {
        List<MemoryLayout> out = new ArrayList<>();
        scalarLayoutsInternal(out, gl);
        return out;
    }

    private static void scalarLayoutsInternal(List<MemoryLayout> out, GroupLayout gl) {
        for (MemoryLayout member : gl.memberLayouts()) {
            if (member instanceof GroupLayout memberGl) {
                scalarLayoutsInternal(out, memberGl);
            } else if (member instanceof SequenceLayout memberSl) {
                for (long i = 0; i < memberSl.elementCount(); i++) {
                    out.add(memberSl.elementLayout());
                }
            } else {
                // padding or value layouts
                out.add(member);
            }
        }
    }

    static boolean isSingleFloatAggregate(MemoryLayout type) {
        List<MemoryLayout> scalarLayouts = scalarLayouts((GroupLayout) type);

        final int numElements = scalarLayouts.size();
        if (numElements > 1 || numElements == 0)
            return false;

        MemoryLayout baseType = scalarLayouts.get(0);

        if (!(baseType instanceof ValueLayout))
            return false;

        TypeClass baseArgClass = classifyValueType((ValueLayout) baseType);
        if (baseArgClass != FLOAT)
            return false;

        return true;
    }

    private static TypeClass classifyStructType(MemoryLayout layout) {

        if (!isRegisterAggregate(layout)) {
            return TypeClass.STRUCT_REFERENCE;
        }

        if (isSingleFloatAggregate(layout)) {
            return TypeClass.STRUCT_SFA;
        }
        return TypeClass.STRUCT_REGISTER;
    }

    public static TypeClass classifyLayout(MemoryLayout type) {
        if (type instanceof ValueLayout) {
            return classifyValueType((ValueLayout) type);
        } else if (type instanceof GroupLayout) {
            return classifyStructType(type);
        } else {
            throw new IllegalArgumentException("Unsupported layout: " + type);
        }
    }
}
