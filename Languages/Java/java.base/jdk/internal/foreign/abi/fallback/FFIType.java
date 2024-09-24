/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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
package jdk.internal.foreign.abi.fallback;

import jdk.internal.foreign.Utils;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.PaddingLayout;
import java.lang.foreign.SequenceLayout;
import java.lang.foreign.StructLayout;
import java.lang.foreign.UnionLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static java.lang.foreign.ValueLayout.JAVA_SHORT;

/**
 * typedef struct _ffi_type
 * {
 *   size_t size;
 *   unsigned short alignment;
 *   unsigned short type;
 *   struct _ffi_type **elements;
 * } ffi_type;
 */
class FFIType {
    private static final ValueLayout SIZE_T = switch ((int) ADDRESS.byteSize()) {
            case 8 -> JAVA_LONG;
            case 4 -> JAVA_INT;
            default -> throw new IllegalStateException("Address size not supported: " + ADDRESS.byteSize());
        };
    private static final ValueLayout UNSIGNED_SHORT = JAVA_SHORT;
    private static final StructLayout LAYOUT = Utils.computePaddedStructLayout(
            SIZE_T, UNSIGNED_SHORT, UNSIGNED_SHORT.withName("type"), ADDRESS.withName("elements"));

    private static final VarHandle VH_TYPE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("type"));
    private static final VarHandle VH_ELEMENTS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("elements"));
    private static final VarHandle VH_SIZE_T_ARRAY = SIZE_T.arrayElementVarHandle();

    private static MemorySegment make(List<MemoryLayout> elements, FFIABI abi, Arena scope) {
        MemorySegment elementsSeg = scope.allocate((elements.size() + 1) * ADDRESS.byteSize());
        int i = 0;
        for (; i < elements.size(); i++) {
            MemoryLayout elementLayout = elements.get(i);
            MemorySegment elementType = toFFIType(elementLayout, abi, scope);
            elementsSeg.setAtIndex(ADDRESS, i, elementType);
        }
        // elements array is null-terminated
        elementsSeg.setAtIndex(ADDRESS, i, MemorySegment.NULL);

        MemorySegment ffiType = scope.allocate(LAYOUT);
        VH_TYPE.set(ffiType, LibFallback.structTag());
        VH_ELEMENTS.set(ffiType, elementsSeg);

        return ffiType;
    }

    private static final Map<Class<?>, MemorySegment> CARRIER_TO_TYPE = Map.of(
        boolean.class, LibFallback.uint8Type(),
        byte.class, LibFallback.sint8Type(),
        short.class, LibFallback.sint16Type(),
        char.class, LibFallback.uint16Type(),
        int.class, LibFallback.sint32Type(),
        long.class, LibFallback.sint64Type(),
        float.class, LibFallback.floatType(),
        double.class, LibFallback.doubleType(),
        MemorySegment.class, LibFallback.pointerType()
    );

    static MemorySegment toFFIType(MemoryLayout layout, FFIABI abi, Arena scope) {
        if (layout instanceof GroupLayout grpl) {
            if (grpl instanceof StructLayout strl) {
                // libffi doesn't want our padding
                List<MemoryLayout> filteredLayouts = strl.memberLayouts().stream()
                        .filter(Predicate.not(PaddingLayout.class::isInstance))
                        .toList();
                MemorySegment structType = make(filteredLayouts, abi, scope);
                verifyStructType(strl, filteredLayouts, structType, abi);
                return structType;
            }
            assert grpl instanceof UnionLayout;
            // JDK-8301800
            throw new IllegalArgumentException("Fallback linker does not support by-value unions: " + grpl);
        } else if (layout instanceof SequenceLayout sl) {
            List<MemoryLayout> elements = Collections.nCopies(Math.toIntExact(sl.elementCount()), sl.elementLayout());
            return make(elements, abi, scope);
        }
        return Objects.requireNonNull(CARRIER_TO_TYPE.get(((ValueLayout) layout).carrier()));
    }

    // verify layout against what libffi sets
    private static void verifyStructType(StructLayout structLayout, List<MemoryLayout> filteredLayouts, MemorySegment structType,
                                         FFIABI abi) {
        try (Arena verifyArena = Arena.ofConfined()) {
            MemorySegment offsetsOut = verifyArena.allocate(SIZE_T.byteSize() * filteredLayouts.size());
            LibFallback.getStructOffsets(structType, offsetsOut, abi);
            long expectedOffset = 0;
            int offsetIdx = 0;
            for (MemoryLayout element : structLayout.memberLayouts()) {
                if (!(element instanceof PaddingLayout)) {
                    long ffiOffset = (long) VH_SIZE_T_ARRAY.get(offsetsOut, offsetIdx++);
                    if (ffiOffset != expectedOffset) {
                        throw new IllegalArgumentException("Invalid group layout." +
                                " Offset of '" + element.name().orElse("<unnamed>")
                                + "': " + expectedOffset + " != " + ffiOffset);
                    }
                }
                expectedOffset += element.byteSize();
            }
        }
    }
}
