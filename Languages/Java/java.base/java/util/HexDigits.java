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
 * Digits class for hexadecimal digits.
 *
 * @since 21
 */
final class HexDigits implements Digits {
    @Stable
    private static final short[] DIGITS;

    /**
     * Singleton instance of HexDigits.
     */
    static final Digits INSTANCE = new HexDigits();

    static {
        short[] digits = new short[16 * 16];

        for (int i = 0; i < 16; i++) {
            short hi = (short) ((i < 10 ? i + '0' : i - 10 + 'a') << 8);

            for (int j = 0; j < 16; j++) {
                short lo = (short) (j < 10 ? j + '0' : j - 10 + 'a');
                digits[(i << 4) + j] = (short) (hi | lo);
            }
        }

        DIGITS = digits;
    }

    /**
     * Constructor.
     */
    private HexDigits() {
    }

    @Override
    public int digits(long value, byte[] buffer, int index,
                      MethodHandle putCharMH) throws Throwable {
        while ((value & ~0xFF) != 0) {
            int digits = DIGITS[(int) (value & 0xFF)];
            value >>>= 8;
            putCharMH.invokeExact(buffer, --index, digits & 0xFF);
            putCharMH.invokeExact(buffer, --index, digits >> 8);
        }

        int digits = DIGITS[(int) (value & 0xFF)];
        putCharMH.invokeExact(buffer, --index, digits & 0xFF);

        if (0xF < value) {
            putCharMH.invokeExact(buffer, --index, digits >> 8);
        }

        return index;
    }

    @Override
    public int size(long value) {
        return value == 0 ? 1 :
                67 - Long.numberOfLeadingZeros(value) >> 2;
    }
}
