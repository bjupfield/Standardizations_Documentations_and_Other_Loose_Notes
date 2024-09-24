/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
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

package sun.net.httpserver;

/**
 * Provides utility methods for checking header field names and quoted strings.
 */
public class Utils {

    // ABNF primitives defined in RFC 7230
    private static final boolean[] TCHAR = new boolean[256];
    private static final boolean[] QDTEXT = new boolean[256];
    private static final boolean[] QUOTED_PAIR = new boolean[256];

    static {
        char[] allowedTokenChars =
                ("!#$%&'*+-.^_`|~0123456789" +
                        "abcdefghijklmnopqrstuvwxyz" +
                        "ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        for (char c : allowedTokenChars) {
            TCHAR[c] = true;
        }
        for (char c = 0x20; c <= 0xFF; c++) {
            QDTEXT[c] = true;
        }
        QDTEXT[0x22] = false;  // (")   illegal
        QDTEXT[0x5c] = false;  // (\)   illegal
        QDTEXT[0x7F] = false;  // (DEL) illegal

        for (char c = 0x20; c <= 0xFF; c++) {
            QUOTED_PAIR[c] = true;
        }
        QUOTED_PAIR[0x09] = true;  // (\t)    legal
        QUOTED_PAIR[0x7F] = false; // (DEL) illegal
    }

    /*
     * Validates an RFC 7230 field-name.
     */
    public static boolean isValidName(String token) {
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c > 255 || !TCHAR[c]) {
                return false;
            }
        }
        return !token.isEmpty();
    }

    /*
     * Validates an RFC 7230 quoted-string.
     */
    public static boolean isQuotedStringContent(String token) {
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c > 255) {
                return false;
            } else if (c == 0x5c) {  // check if valid quoted-pair
                if (i == token.length() - 1 || !QUOTED_PAIR[token.charAt(i++)]) {
                    return false;
                }
            } else if (!QDTEXT[c]) {
                return false; // illegal char
            }
        }
        return true;
    }
}
