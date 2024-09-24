/*
 *  Copyright (c) 2019, 2023, Oracle and/or its affiliates. All rights reserved.
 *  ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 *
 */
package jdk.internal.foreign.layout;

public final class MemoryLayoutUtil {

    private MemoryLayoutUtil() {
    }

    public static long requireNonNegative(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("The provided value was negative: " + value);
        }
        return value;
    }

    public static long requireByteSizeValid(long byteSize, boolean allowZero) {
        if ((byteSize == 0 && !allowZero) || byteSize < 0) {
            throw new IllegalArgumentException("Invalid byte size: " + byteSize);
        }
        return byteSize;
    }

}