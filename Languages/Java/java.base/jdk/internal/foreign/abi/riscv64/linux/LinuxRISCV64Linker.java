/*
 * Copyright (c) 2020, 2023, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2023, Institute of Software, Chinese Academy of Sciences.
 * All rights reserved.
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
 */

package jdk.internal.foreign.abi.riscv64.linux;

import jdk.internal.foreign.abi.AbstractLinker;
import jdk.internal.foreign.abi.LinkerOptions;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.ByteOrder;

public final class LinuxRISCV64Linker extends AbstractLinker {

    public static LinuxRISCV64Linker getInstance() {
        final class Holder {
            private static final LinuxRISCV64Linker INSTANCE = new LinuxRISCV64Linker();
        }

        return Holder.INSTANCE;
    }

    private LinuxRISCV64Linker() {
        // Ensure there is only one instance
    }

    @Override
    protected MethodHandle arrangeDowncall(MethodType inferredMethodType, FunctionDescriptor function, LinkerOptions options) {
        return LinuxRISCV64CallArranger.arrangeDowncall(inferredMethodType, function, options);
    }

    @Override
    protected UpcallStubFactory arrangeUpcall(MethodType targetType, FunctionDescriptor function, LinkerOptions options) {
        return LinuxRISCV64CallArranger.arrangeUpcall(targetType, function, options);
    }

    @Override
    protected ByteOrder linkerByteOrder() {
        return ByteOrder.LITTLE_ENDIAN;
    }
}
