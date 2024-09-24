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

package sun.net.httpserver.simpleserver;

/**
 * A class that represents a URI path segment.
 */
final class URIPathSegment {

    private URIPathSegment() { throw new AssertionError(); }

    /**
     * Checks if the segment of a URI path is supported. For example,
     * "C:" is supported as a drive on Windows only.
     *
     * @param segment the segment string
     * @return true if the segment is supported
     */
    static boolean isSupported(String segment) {
        // apply same logic as WindowsPathParser
        if (segment.length() >= 2 && isLetter(segment.charAt(0)) && segment.charAt(1) == ':') {
            return false;
        }
        return true;
    }

    private static boolean isLetter(char c) {
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
    }
}
