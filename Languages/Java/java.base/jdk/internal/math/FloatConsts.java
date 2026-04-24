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

import static java.lang.Float.MIN_EXPONENT;
import static java.lang.Float.PRECISION;
import static java.lang.Float.SIZE;

/**
 * This class contains additional constants documenting limits of the
 * {@code float} type.
 *
 * @author Joseph D. Darcy
 */

public class FloatConsts {
    /**
     * Don't let anyone instantiate this class.
     */
    private FloatConsts() {}

    /**
     * The number of logical bits in the significand of a
     * {@code float} number, including the implicit bit.
     */
    public static final int SIGNIFICAND_WIDTH = PRECISION;

    /**
     * The exponent the smallest positive {@code float}
     * subnormal value would have if it could be normalized.
     */
    public static final int MIN_SUB_EXPONENT =
            MIN_EXPONENT - (SIGNIFICAND_WIDTH - 1); // -149

    /**
     * Bias used in representing a {@code float} exponent.
     */
    public static final int EXP_BIAS =
            (1 << (SIZE - SIGNIFICAND_WIDTH - 1)) - 1; // 127

    /**
     * Bit mask to isolate the sign bit of a {@code float}.
     */
    public static final int SIGN_BIT_MASK = 1 << (SIZE - 1);

    /**
     * Bit mask to isolate the exponent field of a {@code float}.
     */
    public static final int EXP_BIT_MASK =
            ((1 << (SIZE - SIGNIFICAND_WIDTH)) - 1) << (SIGNIFICAND_WIDTH - 1);

    /**
     * Bit mask to isolate the significand field of a {@code float}.
     */
    public static final int SIGNIF_BIT_MASK = (1 << (SIGNIFICAND_WIDTH - 1)) - 1;

    /**
     * Bit mask to isolate the magnitude bits (combined exponent and
     * significand fields) of a {@code float}.
     */
    public static final int MAG_BIT_MASK = EXP_BIT_MASK | SIGNIF_BIT_MASK;

    static {
        // verify bit masks cover all bit positions and that the bit
        // masks are non-overlapping
        assert(((SIGN_BIT_MASK | EXP_BIT_MASK | SIGNIF_BIT_MASK) == ~0) &&
               (((SIGN_BIT_MASK & EXP_BIT_MASK) == 0) &&
                ((SIGN_BIT_MASK & SIGNIF_BIT_MASK) == 0) &&
                ((EXP_BIT_MASK & SIGNIF_BIT_MASK) == 0)) &&
                ((SIGN_BIT_MASK | MAG_BIT_MASK) == ~0));
    }
}
