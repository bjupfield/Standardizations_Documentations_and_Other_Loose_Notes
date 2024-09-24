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

import jdk.internal.ref.CleanerFactory;

import java.lang.invoke.MethodType;
import java.lang.ref.Cleaner;
import java.util.Arrays;
import java.util.List;

/**
 * This class describes a 'native entry point', which is used as an appendix argument to linkToNative calls.
 */
public class NativeEntryPoint {
    static {
        registerNatives();
    }

    private final MethodType methodType;
    private final long downcallStubAddress; // read by VM

    private static final Cleaner CLEANER = CleanerFactory.cleaner();
    private static final SoftReferenceCache<CacheKey, NativeEntryPoint> NEP_CACHE = new SoftReferenceCache<>();
    private record CacheKey(MethodType methodType, ABIDescriptor abi,
                            List<VMStorage> argMoves, List<VMStorage> retMoves,
                            boolean needsReturnBuffer, int capturedStateMask,
                            boolean needsTransition) {}

    private NativeEntryPoint(MethodType methodType, long downcallStubAddress) {
        this.methodType = methodType;
        this.downcallStubAddress = downcallStubAddress;
    }

    public static NativeEntryPoint make(ABIDescriptor abi,
                                        VMStorage[] argMoves, VMStorage[] returnMoves,
                                        MethodType methodType,
                                        boolean needsReturnBuffer,
                                        int capturedStateMask,
                                        boolean needsTransition) {
        if (returnMoves.length > 1 != needsReturnBuffer) {
            throw new AssertionError("Multiple register return, but needsReturnBuffer was false");
        }
        checkType(methodType, needsReturnBuffer, capturedStateMask);

        CacheKey key = new CacheKey(methodType, abi, Arrays.asList(argMoves), Arrays.asList(returnMoves),
                                    needsReturnBuffer, capturedStateMask, needsTransition);
        return NEP_CACHE.get(key, k -> {
            long downcallStub = makeDowncallStub(methodType, abi, argMoves, returnMoves, needsReturnBuffer,
                                                 capturedStateMask, needsTransition);
            NativeEntryPoint nep = new NativeEntryPoint(methodType, downcallStub);
            CLEANER.register(nep, () -> freeDowncallStub(downcallStub));
            return nep;
        });
    }

    private static void checkType(MethodType methodType, boolean needsReturnBuffer, int savedValueMask) {
        if (methodType.parameterType(0) != long.class) {
            throw new AssertionError("Address expected as first param: " + methodType);
        }
        int checkIdx = 1;
        if ((needsReturnBuffer && methodType.parameterType(checkIdx++) != long.class)
            || (savedValueMask != 0 && methodType.parameterType(checkIdx) != long.class)) {
            throw new AssertionError("return buffer and/or preserved value address expected: " + methodType);
        }
    }

    private static native long makeDowncallStub(MethodType methodType, ABIDescriptor abi,
                                                VMStorage[] encArgMoves, VMStorage[] encRetMoves,
                                                boolean needsReturnBuffer,
                                                int capturedStateMask,
                                                boolean needsTransition);

    private static native boolean freeDowncallStub0(long downcallStub);
    private static void freeDowncallStub(long downcallStub) {
        if (!freeDowncallStub0(downcallStub)) {
            throw new InternalError("Could not free downcall stub");
        }
    }

    public MethodType type() {
        return methodType;
    }

    private static native void registerNatives();
}
