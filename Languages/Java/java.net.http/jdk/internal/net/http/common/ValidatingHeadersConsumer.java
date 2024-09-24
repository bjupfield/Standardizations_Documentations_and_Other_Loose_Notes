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
package jdk.internal.net.http.common;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Set;

/*
 * Checks RFC 9113 rules (relaxed) compliance regarding pseudo-headers.
 */
public class ValidatingHeadersConsumer {

    private static final Set<String> PSEUDO_HEADERS =
            Set.of(":authority", ":method", ":path", ":scheme", ":status");

    /** Used to check that if there are pseudo-headers, they go first */
    private boolean pseudoHeadersEnded;

    /**
     * Called when END_HEADERS was received. This consumer may be invoked
     * again after reset() is called, but for a whole new set of headers.
     */
    public void reset() {
        pseudoHeadersEnded = false;
    }

    /**
     * Called when a header field (name, value) pair has been decoded
     * @param name    the decoded name
     * @param value   the decoded value
     * @throws UncheckedIOException if the name or value are illegal
     */
    public void onDecoded(CharSequence name, CharSequence value)
            throws UncheckedIOException
    {
        String n = name.toString();
        if (n.startsWith(":")) {
            if (pseudoHeadersEnded) {
                throw newException("Unexpected pseudo-header '%s'", n);
            } else if (!PSEUDO_HEADERS.contains(n)) {
                throw newException("Unknown pseudo-header '%s'", n);
            }
        } else {
            pseudoHeadersEnded = true;
            // RFC-9113, section 8.2.1 for HTTP/2 and RFC-9114, section 4.2 state that
            // header name MUST be lowercase (and allowed characters)
            if (!Utils.isValidLowerCaseName(n)) {
                throw newException("Bad header name '%s'", n);
            }
        }
        String v = value.toString();
        if (!Utils.isValidValue(v)) {
            throw newException("Bad header value '%s'", v);
        }
    }

    protected String formatMessage(String message, String header) {
        return String.format(message, header);
    }

    protected UncheckedIOException newException(String message, String header)
    {
        return new UncheckedIOException(
                new IOException(formatMessage(message, header)));
    }
}
