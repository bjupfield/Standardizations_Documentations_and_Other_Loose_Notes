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

package java.util;

import java.lang.invoke.MethodHandle;

import jdk.internal.vm.annotation.Stable;

/**
 * Digits class for octal digits.
 *
 * @since 21
 */
final class OctalDigits implements Digits {
    @Stable
    private static final short[] DIGITS;

    /**
     * Singleton instance of OctalDigits.
     */
    static final Digits INSTANCE = new OctalDigits();

    static {
        short[] digits = new short[8 * 8];

        for (int i = 0; i < 8; i++) {
            short hi = (short) ((i + '0') << 8);

            for (int j = 0; j < 8; j++) {
                short lo = (short) (j + '0');
                digits[(i << 3) + j] = (short) (hi | lo);
            }
        }

        DIGITS = digits;
    }

    /**
     * Constructor.
     */
    private OctalDigits() {
    }

    @Override
    public int digits(long value, byte[] buffer, int index,
                      MethodHandle putCharMH) throws Throwable {
        while ((value & ~0x3F) != 0) {
            int digits = DIGITS[(int) (value & 0x3F)];
            value >>>= 6;
            putCharMH.invokeExact(buffer, --index, digits & 0xFF);
            putCharMH.invokeExact(buffer, --index, digits >> 8);
        }

        int digits = DIGITS[(int) (value & 0x3F)];
        putCharMH.invokeExact(buffer, --index, digits & 0xFF);

        if (7 < value) {
            putCharMH.invokeExact(buffer, --index, digits >> 8);
        }

        return index;
    }

    @Override
    public int size(long value) {
        return (66 - Long.numberOfLeadingZeros(value)) / 3;
    }
}
