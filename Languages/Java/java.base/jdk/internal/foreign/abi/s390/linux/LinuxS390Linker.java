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

import jdk.internal.foreign.abi.AbstractLinker;
import jdk.internal.foreign.abi.LinkerOptions;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.ByteOrder;

public final class LinuxS390Linker extends AbstractLinker {

    public static LinuxS390Linker getInstance() {
        final class Holder {
            private static final LinuxS390Linker INSTANCE = new LinuxS390Linker();
        }

        return Holder.INSTANCE;
    }

    private LinuxS390Linker() {
        // Ensure there is only one instance
    }

    @Override
    protected MethodHandle arrangeDowncall(MethodType inferredMethodType, FunctionDescriptor function, LinkerOptions options) {
        return LinuxS390CallArranger.arrangeDowncall(inferredMethodType, function, options);
    }

    @Override
    protected UpcallStubFactory arrangeUpcall(MethodType targetType, FunctionDescriptor function, LinkerOptions options) {
        return LinuxS390CallArranger.arrangeUpcall(targetType, function, options);
    }

    @Override
    protected ByteOrder linkerByteOrder() {
        return ByteOrder.BIG_ENDIAN;
    }
}
