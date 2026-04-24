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
package jdk.jfr.internal.util;

public final class Matcher {

    /**
     * Returns true if text matches pattern of characters, '*' and '?'
     */
    public static boolean match(String text, String pattern) {
        if (pattern.length() == 0) {
            // empty filter string matches if string is empty
            return text.length() == 0;
        }
        if (pattern.charAt(0) == '*') { // recursive check
            pattern = pattern.substring(1);
            for (int n = 0; n <= text.length(); n++) {
                if (match(text.substring(n), pattern))
                    return true;
            }
        } else if (text.length() == 0) {
            // empty string and non-empty filter does not match
            return false;
        } else if (pattern.charAt(0) == '?') {
            // eat any char and move on
            return match(text.substring(1), pattern.substring(1));
        } else if (pattern.charAt(0) == text.charAt(0)) {
            // eat chars and move on
            return match(text.substring(1), pattern.substring(1));
        }
        return false;
    }
}
