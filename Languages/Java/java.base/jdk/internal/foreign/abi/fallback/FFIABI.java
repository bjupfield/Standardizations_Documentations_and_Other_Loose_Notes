/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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
package jdk.internal.foreign.abi.fallback;

/**
 * enum which maps the {@code ffi_abi} enum
 */
enum FFIABI {
    DEFAULT(LibFallback.defaultABI());

    private final int value;

    FFIABI(int abi) {
        this.value = abi;
    }

    int value() {
        return value;
    }
}
