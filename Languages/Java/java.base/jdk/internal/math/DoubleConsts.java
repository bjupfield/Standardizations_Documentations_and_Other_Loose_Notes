/*
 * Copyright (c) 2003, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.internal.math;

import static java.lang.Double.MIN_EXPONENT;
import static java.lang.Double.PRECISION;
import static java.lang.Double.SIZE;

/**
 * This class contains additional constants documenting limits of the
 * {@code double} type.
 *
 * @author Joseph D. Darcy
 */

public class DoubleConsts {
    /**
     * Don't let anyone instantiate this class.
     */
    private DoubleConsts() {}

    /**
     * The number of logical bits in the significand of a
     * {@code double} number, including the implicit bit.
     */
    public static final int SIGNIFICAND_WIDTH = PRECISION;

    /**
     * The exponent the smallest positive {@code double}
     * subnormal value would have if it could be normalized..
     */
    public static final int MIN_SUB_EXPONENT =
            MIN_EXPONENT - (SIGNIFICAND_WIDTH - 1); // -1074

    /**
     * Bias used in representing a {@code double} exponent.
     */
    public static final int EXP_BIAS =
            (1 << (SIZE - SIGNIFICAND_WIDTH - 1)) - 1; // 1023

    /**
     * Bit mask to isolate the sign bit of a {@code double}.
     */
    public static final long SIGN_BIT_MASK = 1L << (SIZE - 1);

    /**
     * Bit mask to isolate the exponent field of a {@code double}.
     */
    public static final long EXP_BIT_MASK =
            ((1L << (SIZE - SIGNIFICAND_WIDTH)) - 1) << (SIGNIFICAND_WIDTH - 1);

    /**
     * Bit mask to isolate the significand field of a {@code double}.
     */
    public static final long SIGNIF_BIT_MASK = (1L << (SIGNIFICAND_WIDTH - 1)) - 1;

    /**
     * Bit mask to isolate the magnitude bits (combined exponent and
     * significand fields) of a {@code double}.
     */
    public static final long MAG_BIT_MASK = EXP_BIT_MASK | SIGNIF_BIT_MASK;

    static {
        // verify bit masks cover all bit positions and that the bit
        // masks are non-overlapping
        assert(((SIGN_BIT_MASK | EXP_BIT_MASK | SIGNIF_BIT_MASK) == ~0L) &&
               (((SIGN_BIT_MASK & EXP_BIT_MASK) == 0L) &&
                ((SIGN_BIT_MASK & SIGNIF_BIT_MASK) == 0L) &&
                ((EXP_BIT_MASK & SIGNIF_BIT_MASK) == 0L)) &&
                ((SIGN_BIT_MASK | MAG_BIT_MASK) == ~0L));
    }
}
