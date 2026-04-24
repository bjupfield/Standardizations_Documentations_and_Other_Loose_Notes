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

/**
 * Digits provides a fast methodology for converting integers and longs to
 * ASCII strings.
 *
 * @since 21
 */
sealed interface Digits permits DecimalDigits, HexDigits, OctalDigits {
    /**
     * Insert digits for long value in buffer from high index to low index.
     *
     * @param value      value to convert
     * @param buffer     byte buffer to copy into
     * @param index      insert point + 1
     * @param putCharMH  method to put character
     *
     * @return the last index used
     *
     * @throws Throwable if putCharMH fails (unusual).
     */
    int digits(long value, byte[] buffer, int index,
               MethodHandle putCharMH) throws Throwable;

    /**
     * Calculate the number of digits required to represent the long.
     *
     * @param value value to convert
     *
     * @return number of digits
     */
    int size(long value);

}
