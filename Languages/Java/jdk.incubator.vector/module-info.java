/*
 * Copyright (c) 2017, 2022, Oracle and/or its affiliates. All rights reserved.
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

import jdk.internal.javac.ParticipatesInPreview;

/**
 * Defines an API for expressing computations that can be reliably compiled
 * at runtime into SIMD instructions, such as AVX instructions on x64, and
 * NEON instructions on AArch64.
 * {@Incubating}
 *
 * @moduleGraph
 */
@ParticipatesInPreview
module jdk.incubator.vector {
    exports jdk.incubator.vector;
}
