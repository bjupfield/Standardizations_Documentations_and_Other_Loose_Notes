/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021, Arm Limited. All rights reserved.
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
package jdk.internal.foreign.abi.aarch64.linux;

import jdk.internal.foreign.abi.aarch64.CallArranger;
import jdk.internal.foreign.abi.ABIDescriptor;

/**
 * AArch64 CallArranger specialized for Linux ABI.
 */
public class LinuxAArch64CallArranger extends CallArranger {

    @Override
    protected boolean varArgsOnStack() {
        // Variadic arguments are passed as normal arguments
        return false;
    }

    @Override
    protected boolean requiresSubSlotStackPacking() {
        return false;
    }

    @Override
    protected ABIDescriptor abiDescriptor() {
        return C;
    }

    @Override
    protected boolean useIntRegsForVariadicFloatingPointArgs() {
        return false;
    }

    @Override
    protected boolean spillsVariadicStructsPartially() {
        return false;
    }

}
