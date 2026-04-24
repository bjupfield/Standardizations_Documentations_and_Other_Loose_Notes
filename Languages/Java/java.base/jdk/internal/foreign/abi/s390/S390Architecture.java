/*
 * Copyright (c) 2020, 2023, Oracle and/or its affiliates. All rights reserved.
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
package jdk.internal.foreign.abi.s390;

import jdk.internal.foreign.abi.ABIDescriptor;
import jdk.internal.foreign.abi.Architecture;
import jdk.internal.foreign.abi.StubLocations;
import jdk.internal.foreign.abi.VMStorage;

public final class S390Architecture implements Architecture {
    public static final Architecture INSTANCE = new S390Architecture();

    // Needs to be consistent with vmstorage_s390.hpp.
    public static final short REG32_MASK = 0b0000_0000_0000_0001;
    public static final short REG64_MASK = 0b0000_0000_0000_0011;

    private static final int INTEGER_REG_SIZE = 8;
    private static final int FLOAT_REG_SIZE = 8;
    private static final int STACK_SLOT_SIZE = 8;

    // Suppresses default constructor, ensuring non-instantiability.
    private S390Architecture() {
    }

    @Override
    public boolean isStackType(int cls) {
        return cls == StorageType.STACK;
    }

    @Override
    public int typeSize(int cls) {
        switch (cls) {
            case StorageType.INTEGER:
                return INTEGER_REG_SIZE;
            case StorageType.FLOAT:
                return FLOAT_REG_SIZE;
                // STACK is deliberately omitted
        }

        throw new IllegalArgumentException("Invalid Storage Class: " + cls);
    }

    public interface StorageType {
        byte INTEGER = 0;
        byte FLOAT = 1;
        byte STACK = 2;
        byte PLACEHOLDER = 3;
    }

    public static class Regs { // break circular dependency
        public static final VMStorage r0 = integerRegister(0);
        public static final VMStorage r1 = integerRegister(1);
        public static final VMStorage r2 = integerRegister(2);
        public static final VMStorage r3 = integerRegister(3);
        public static final VMStorage r4 = integerRegister(4);
        public static final VMStorage r5 = integerRegister(5);
        public static final VMStorage r6 = integerRegister(6);
        public static final VMStorage r7 = integerRegister(7);
        public static final VMStorage r8 = integerRegister(8);
        public static final VMStorage r9 = integerRegister(9);
        public static final VMStorage r10 = integerRegister(10);
        public static final VMStorage r11 = integerRegister(11);
        public static final VMStorage r12 = integerRegister(12);
        public static final VMStorage r13 = integerRegister(13);
        public static final VMStorage r14 = integerRegister(14);
        public static final VMStorage r15 = integerRegister(15);

        public static final VMStorage f0 = floatRegister(0);
        public static final VMStorage f1 = floatRegister(1);
        public static final VMStorage f2 = floatRegister(2);
        public static final VMStorage f3 = floatRegister(3);
        public static final VMStorage f4 = floatRegister(4);
        public static final VMStorage f5 = floatRegister(5);
        public static final VMStorage f6 = floatRegister(6);
        public static final VMStorage f7 = floatRegister(7);
        public static final VMStorage f8 = floatRegister(8);
        public static final VMStorage f9 = floatRegister(9);
        public static final VMStorage f10 = floatRegister(10);
        public static final VMStorage f11 = floatRegister(11);
        public static final VMStorage f12 = floatRegister(12);
        public static final VMStorage f13 = floatRegister(13);
        public static final VMStorage f14 = floatRegister(14);
        public static final VMStorage f15 = floatRegister(15);
    }

    private static VMStorage integerRegister(int index) {
        return new VMStorage(StorageType.INTEGER, REG64_MASK, index, "r" + index);
    }

    private static VMStorage floatRegister(int index) {
        return new VMStorage(StorageType.FLOAT, REG64_MASK, index, "f" + index);
    }

    public static VMStorage stackStorage(short size, int byteOffset) {
        return new VMStorage(StorageType.STACK, size, byteOffset);
    }

    public static ABIDescriptor abiFor(VMStorage[] inputIntRegs,
                                       VMStorage[] inputFloatRegs,
                                       VMStorage[] outputIntRegs,
                                       VMStorage[] outputFloatRegs,
                                       VMStorage[] volatileIntRegs,
                                       VMStorage[] volatileFloatRegs,
                                       int stackAlignment,
                                       int shadowSpace,
                                       VMStorage scratch1, VMStorage scratch2) {
        return new ABIDescriptor(
            INSTANCE,
            new VMStorage[][] {
                inputIntRegs,
                inputFloatRegs,
            },
            new VMStorage[][] {
                outputIntRegs,
                outputFloatRegs,
            },
            new VMStorage[][] {
                volatileIntRegs,
                volatileFloatRegs,
            },
            stackAlignment,
            shadowSpace,
            scratch1, scratch2,
            StubLocations.TARGET_ADDRESS.storage(StorageType.PLACEHOLDER),
            StubLocations.RETURN_BUFFER.storage(StorageType.PLACEHOLDER),
            StubLocations.CAPTURED_STATE_BUFFER.storage(StorageType.PLACEHOLDER));
    }
}
