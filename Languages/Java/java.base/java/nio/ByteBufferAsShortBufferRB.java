/*
 * Copyright (c) 2000, 2023, Oracle and/or its affiliates. All rights reserved.
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

// -- This file was mechanically generated: Do not edit! -- //

package java.nio;

import java.lang.foreign.MemorySegment;
import java.util.Objects;
import jdk.internal.misc.Unsafe;




final

class ByteBufferAsShortBufferRB                  // package-private
    extends ByteBufferAsShortBufferB



{







    ByteBufferAsShortBufferRB(ByteBuffer bb, MemorySegment segment) {   // package-private












        super(bb, segment);

    }

    ByteBufferAsShortBufferRB(ByteBuffer bb,
                                     int mark, int pos, int lim, int cap,
                                     long addr, MemorySegment segment)
    {






        super(bb, mark, pos, lim, cap, addr, segment);

    }

    @Override
    Object base() {
        return bb.hb;
    }

    public ShortBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        int rem = (pos <= lim ? lim - pos : 0);
        long addr = byteOffset(pos);
        return new ByteBufferAsShortBufferRB(bb, -1, 0, rem, rem, addr, segment);
    }

    @Override
    public ShortBuffer slice(int index, int length) {
        Objects.checkFromIndexSize(index, length, limit());
        return new ByteBufferAsShortBufferRB(bb,
                                                    -1,
                                                    0,
                                                    length,
                                                    length,
                                                    byteOffset(index), segment);
    }

    public ShortBuffer duplicate() {
        return new ByteBufferAsShortBufferRB(bb,
                                                    this.markValue(),
                                                    this.position(),
                                                    this.limit(),
                                                    this.capacity(),
                                                    address, segment);
    }

    public ShortBuffer asReadOnlyBuffer() {








        return duplicate();

    }


































    public ShortBuffer put(short x) {






        throw new ReadOnlyBufferException();

    }

    public ShortBuffer put(int i, short x) {






        throw new ReadOnlyBufferException();

    }

    public ShortBuffer compact() {

















        throw new ReadOnlyBufferException();

    }

    public boolean isDirect() {
        return bb.isDirect();
    }

    public boolean isReadOnly() {
        return true;
    }





































    public ByteOrder order() {

        return ByteOrder.BIG_ENDIAN;




    }






}
