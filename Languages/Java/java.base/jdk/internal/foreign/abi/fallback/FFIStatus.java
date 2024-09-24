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
 * See doc: <a href="https://github.com/libffi/libffi/blob/7611bb4cfe90884b55ad225e0166136a1d2cf22b/doc/libffi.texi#L159"></a>
 * <p>
 * typedef enum {
 *   FFI_OK = 0,
 *   FFI_BAD_TYPEDEF,
 *   FFI_BAD_ABI,
 *   FFI_BAD_ARGTYPE
 * } ffi_status;
 */
enum FFIStatus {
    FFI_OK,
    FFI_BAD_TYPEDEF,
    FFI_BAD_ABI,
    FFI_BAD_ARGTYPE;

    static FFIStatus of(int code) {
        return switch (code) {
            case 0 -> FFI_OK;
            case 1 -> FFI_BAD_TYPEDEF;
            case 2 -> FFI_BAD_ABI;
            case 3 -> FFI_BAD_ARGTYPE;
            default -> throw new IllegalArgumentException("Unknown status code: " + code);
        };
    }
}
