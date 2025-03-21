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


sealed



class ByteBufferAsShortBufferL                  // package-private
    extends ShortBuffer

    permits ByteBufferAsShortBufferRL

{



    protected final ByteBuffer bb;



    ByteBufferAsShortBufferL(ByteBuffer bb, MemorySegment segment) {   // package-private

        super(-1, 0,
              bb.remaining() >> 1,
              bb.remaining() >> 1, segment);
        this.bb = bb;
        // enforce limit == capacity
        int cap = this.capacity();
        this.limit(cap);
        int pos = this.position();
        assert (pos <= cap);
        address = bb.address;



    }

    ByteBufferAsShortBufferL(ByteBuffer bb,
                                     int mark, int pos, int lim, int cap,
                                     long addr, MemorySegment segment)
    {

        super(mark, pos, lim, cap, segment);
        this.bb = bb;
        address = addr;
        assert address >= bb.address;



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
        return new ByteBufferAsShortBufferL(bb, -1, 0, rem, rem, addr, segment);
    }

    @Override
    public ShortBuffer slice(int index, int length) {
        Objects.checkFromIndexSize(index, length, limit());
        return new ByteBufferAsShortBufferL(bb,
                                                    -1,
                                                    0,
                                                    length,
                                                    length,
                                                    byteOffset(index), segment);
    }

    public ShortBuffer duplicate() {
        return new ByteBufferAsShortBufferL(bb,
                                                    this.markValue(),
                                                    this.position(),
                                                    this.limit(),
                                                    this.capacity(),
                                                    address, segment);
    }

    public ShortBuffer asReadOnlyBuffer() {

        return new ByteBufferAsShortBufferRL(bb,
                                                 this.markValue(),
                                                 this.position(),
                                                 this.limit(),
                                                 this.capacity(),
                                                 address, segment);



    }



    private int ix(int i) {
        int off = (int) (address - bb.address);
        return (i << 1) + off;
    }

    protected long byteOffset(long i) {
        return (i << 1) + address;
    }

    public short get() {
        short x = SCOPED_MEMORY_ACCESS.getShortUnaligned(session(), bb.hb, byteOffset(nextGetIndex()),
            false);
        return (x);
    }

    public short get(int i) {
        short x = SCOPED_MEMORY_ACCESS.getShortUnaligned(session(), bb.hb, byteOffset(checkIndex(i)),
            false);
        return (x);
    }











    public ShortBuffer put(short x) {

        short y = (x);
        SCOPED_MEMORY_ACCESS.putShortUnaligned(session(), bb.hb, byteOffset(nextPutIndex()), y,
            false);
        return this;



    }

    public ShortBuffer put(int i, short x) {

        short y = (x);
        SCOPED_MEMORY_ACCESS.putShortUnaligned(session(), bb.hb, byteOffset(checkIndex(i)), y,
            false);
        return this;



    }

    public ShortBuffer compact() {

        int pos = position();
        int lim = limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);

        ByteBuffer db = bb.duplicate();
        db.limit(ix(lim));
        db.position(ix(0));
        ByteBuffer sb = db.slice();
        sb.position(pos << 1);
        sb.compact();
        position(rem);
        limit(capacity());
        discardMark();
        return this;



    }

    public boolean isDirect() {
        return bb.isDirect();
    }

    public boolean isReadOnly() {
        return false;
    }





































    public ByteOrder order() {




        return ByteOrder.LITTLE_ENDIAN;

    }






}
