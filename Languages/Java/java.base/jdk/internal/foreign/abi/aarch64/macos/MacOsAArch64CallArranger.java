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
package jdk.internal.foreign.abi.aarch64.macos;

import jdk.internal.foreign.abi.aarch64.CallArranger;
import jdk.internal.foreign.abi.ABIDescriptor;

/**
 * AArch64 CallArranger specialized for macOS ABI.
 */
public class MacOsAArch64CallArranger extends CallArranger {

    @Override
    protected boolean varArgsOnStack() {
        // Variadic arguments are always passed on the stack
        return true;
    }

    @Override
    protected boolean requiresSubSlotStackPacking() {
        return true;
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
