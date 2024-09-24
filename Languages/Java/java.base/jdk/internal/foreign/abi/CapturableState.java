/*
 * Copyright (c) 2022, 2023, Oracle and/or its affiliates. All rights reserved.
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

import jdk.internal.foreign.Utils;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.foreign.ValueLayout.JAVA_INT;
import static sun.security.action.GetPropertyAction.privilegedGetProperty;

public enum CapturableState {
    GET_LAST_ERROR    ("GetLastError",    JAVA_INT, 1 << 0, Utils.IS_WINDOWS),
    WSA_GET_LAST_ERROR("WSAGetLastError", JAVA_INT, 1 << 1, Utils.IS_WINDOWS),
    ERRNO             ("errno",           JAVA_INT, 1 << 2, true);

    public static final StructLayout LAYOUT = MemoryLayout.structLayout(
        supportedStates().map(CapturableState::layout).toArray(MemoryLayout[]::new));

    private final String stateName;
    private final ValueLayout layout;
    private final int mask;
    private final boolean isSupported;

    CapturableState(String stateName, ValueLayout layout, int mask, boolean isSupported) {
        this.stateName = stateName;
        this.layout = layout.withName(stateName);
        this.mask = mask;
        this.isSupported = isSupported;
    }

    private static Stream<CapturableState> supportedStates() {
        return Stream.of(values()).filter(CapturableState::isSupported);
    }

    public static CapturableState forName(String name) {
        return Stream.of(values())
                .filter(stl -> stl.stateName().equals(name))
                .filter(CapturableState::isSupported)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown name: " + name +", must be one of: "
                            + supportedStates()
                                    .map(CapturableState::stateName)
                                    .collect(Collectors.joining(", "))));
    }

    public String stateName() {
        return stateName;
    }

    public ValueLayout layout() {
        return layout;
    }

    public int mask() {
        return mask;
    }

    public boolean isSupported() {
        return isSupported;
    }
}
