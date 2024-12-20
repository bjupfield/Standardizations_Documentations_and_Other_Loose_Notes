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

package sun.nio.cs.ext;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import sun.nio.cs.DoubleByte;
import sun.nio.cs.HistoricallyNamedCharset;
import static sun.nio.cs.CharsetMapping.*;

public class IBM942C extends Charset implements HistoricallyNamedCharset
{
    public IBM942C() {
        super("x-IBM942C", ExtendedCharsets.aliasesFor("x-IBM942C"));
    }

    public String historicalName() {
        return "Cp942C";
    }

    public boolean contains(Charset cs) {
        return ((cs.name().equals("US-ASCII"))
                || (cs instanceof IBM942C));
    }

    public CharsetDecoder newDecoder() {
        return new DoubleByte.Decoder(this,
                                      IBM942.DecodeHolder.b2c,
                                      Holder.b2cSB,
                                      0x40,
                                      0xfc);
    }

    public CharsetEncoder newEncoder() {
        return new DoubleByte.Encoder(this, Holder.c2b, Holder.c2bIndex);
    }

    private static class Holder {
        static final char[] b2cSB;
        static final char[] c2b;
        static final char[] c2bIndex;

        static {
            // the mappings that need updating are
            //    u+001a  <-> 0x1a
            //    u+001c  <-> 0x1c
            //    u+005c  <-> 0x5c
            //    u+007e  <-> 0x7e
            //    u+007f  <-> 0x7f

            b2cSB = Arrays.copyOf(IBM942.DecodeHolder.b2cSB, IBM942.DecodeHolder.b2cSB.length);
            b2cSB[0x1a] = 0x1a;
            b2cSB[0x1c] = 0x1c;
            b2cSB[0x5c] = 0x5c;
            b2cSB[0x7e] = 0x7e;
            b2cSB[0x7f] = 0x7f;

            c2b = Arrays.copyOf(IBM942.EncodeHolder.c2b, IBM942.EncodeHolder.c2b.length);
            c2bIndex = Arrays.copyOf(IBM942.EncodeHolder.c2bIndex, IBM942.EncodeHolder.c2bIndex.length);
            c2b[c2bIndex[0] + 0x1a] = 0x1a;
            c2b[c2bIndex[0] + 0x1c] = 0x1c;
            c2b[c2bIndex[0] + 0x5c] = 0x5c;
            c2b[c2bIndex[0] + 0x7e] = 0x7e;
            c2b[c2bIndex[0] + 0x7f] = 0x7f;
        }
    }
}
