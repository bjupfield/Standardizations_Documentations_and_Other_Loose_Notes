/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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

package sun.nio.ch.sctp;

/**
 * Utility class used by implementations on platforms <em>not</em> supporting SCTP.
 * <p>
 * This class is not present on the "unix" platform because unix can support SCTP.
 */
public final class UnsupportedUtil {

    private static final String MESSAGE = "SCTP not supported on this platform";

    // Suppresses default constructor, ensuring non-instantiability.
    private UnsupportedUtil() {}

    static UnsupportedOperationException sctpUnsupported() {
        return new UnsupportedOperationException(MESSAGE);
    }

}
