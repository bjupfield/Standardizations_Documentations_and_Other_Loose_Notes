/*
 * Copyright (c) 2020, 2022, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Carrier class used to communicate with the VM
 *
 * It is particularly low-level since the VM will be accessing these fields directly
 */
public class ABIDescriptor {
    final Architecture arch;

    public final VMStorage[][] inputStorage;
    public final VMStorage[][] outputStorage;

    final VMStorage[][] volatileStorage;

    final int stackAlignment;
    final int shadowSpace;

    final VMStorage scratch1;
    final VMStorage scratch2;

    final VMStorage targetAddrStorage;
    final VMStorage retBufAddrStorage;
    final VMStorage capturedStateStorage;

    public ABIDescriptor(Architecture arch, VMStorage[][] inputStorage, VMStorage[][] outputStorage,
                         VMStorage[][] volatileStorage, int stackAlignment, int shadowSpace,
                         VMStorage scratch1, VMStorage scratch2,
                         VMStorage targetAddrStorage, VMStorage retBufAddrStorage,
                         VMStorage capturedStateStorage) {
        this.arch = arch;
        this.inputStorage = inputStorage;
        this.outputStorage = outputStorage;
        this.volatileStorage = volatileStorage;
        this.stackAlignment = stackAlignment;
        this.shadowSpace = shadowSpace;
        this.scratch1 = scratch1;
        this.scratch2 = scratch2;
        this.targetAddrStorage = targetAddrStorage;
        this.retBufAddrStorage = retBufAddrStorage;
        this.capturedStateStorage = capturedStateStorage;
    }

    public VMStorage targetAddrStorage() {
        return targetAddrStorage;
    }

    public VMStorage retBufAddrStorage() {
        return retBufAddrStorage;
    }

    public VMStorage capturedStateStorage() {
        return capturedStateStorage;
    }
}
