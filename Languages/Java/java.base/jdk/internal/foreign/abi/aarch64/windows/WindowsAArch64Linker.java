/*
 * Copyright (c) 2021, 2023, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021, Arm Limited. All rights reserved.
 * Copyright (c) 2021, 2022, Microsoft. All rights reserved.
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
package jdk.internal.foreign.abi.aarch64.windows;

import jdk.internal.foreign.abi.AbstractLinker;
import jdk.internal.foreign.abi.LinkerOptions;
import jdk.internal.foreign.abi.aarch64.CallArranger;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.ByteOrder;

/**
 * ABI implementation for Windows/AArch64. Based on AAPCS with
 * changes to va_list.
 */
public final class WindowsAArch64Linker extends AbstractLinker {
    private static WindowsAArch64Linker instance;

    public static WindowsAArch64Linker getInstance() {
        if (instance == null) {
            instance = new WindowsAArch64Linker();
        }
        return instance;
    }

    @Override
    protected MethodHandle arrangeDowncall(MethodType inferredMethodType, FunctionDescriptor function, LinkerOptions options) {
        return CallArranger.WINDOWS.arrangeDowncall(inferredMethodType, function, options);
    }

    @Override
    protected UpcallStubFactory arrangeUpcall(MethodType targetType, FunctionDescriptor function, LinkerOptions options) {
        return CallArranger.WINDOWS.arrangeUpcall(targetType, function, options);
    }

    @Override
    protected ByteOrder linkerByteOrder() {
        return ByteOrder.LITTLE_ENDIAN;
    }
}
