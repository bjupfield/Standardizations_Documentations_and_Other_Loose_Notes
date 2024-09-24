/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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
 */
package jdk.vm.ci.hotspot.riscv64;

import jdk.vm.ci.hotspot.HotSpotVMConfigAccess;
import jdk.vm.ci.hotspot.HotSpotVMConfigStore;
import jdk.vm.ci.services.Services;

/**
 * Used to access native configuration details.
 *
 * All non-static, public fields in this class are so that they can be compiled as constants.
 */
class RISCV64HotSpotVMConfig extends HotSpotVMConfigAccess {

    RISCV64HotSpotVMConfig(HotSpotVMConfigStore config) {
        super(config);
    }

    final boolean linuxOs = Services.getSavedProperty("os.name", "").startsWith("Linux");

    final boolean useCompressedOops = getFlag("UseCompressedOops", Boolean.class);

    // CPU Capabilities

    /*
     * These flags are set based on the corresponding command line flags.
     */
    final boolean useConservativeFence = getFlag("UseConservativeFence", Boolean.class);
    final boolean avoidUnalignedAccesses = getFlag("AvoidUnalignedAccesses", Boolean.class);
    final boolean nearCpool = getFlag("NearCpool", Boolean.class);
    final boolean traceTraps = getFlag("TraceTraps", Boolean.class);
    final boolean useRVV = getFlag("UseRVV", Boolean.class);
    final boolean useRVC = getFlag("UseRVC", Boolean.class);
    final boolean useZba = getFlag("UseZba", Boolean.class);
    final boolean useZbb = getFlag("UseZbb", Boolean.class);
    final boolean useRVVForBigIntegerShiftIntrinsics = getFlag("UseRVVForBigIntegerShiftIntrinsics", Boolean.class);

    final long vmVersionFeatures = getFieldValue("Abstract_VM_Version::_features", Long.class, "uint64_t");
}
