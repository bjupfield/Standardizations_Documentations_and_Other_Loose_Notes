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



class ByteBufferAsCharBufferL                  // package-private
    extends CharBuffer

    permits ByteBufferAsCharBufferRL

{



    protected final ByteBuffer bb;



    ByteBufferAsCharBufferL(ByteBuffer bb, MemorySegment segment) {   // package-private

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

    ByteBufferAsCharBufferL(ByteBuffer bb,
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

    public CharBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        int rem = (pos <= lim ? lim - pos : 0);
        long addr = byteOffset(pos);
        return new ByteBufferAsCharBufferL(bb, -1, 0, rem, rem, addr, segment);
    }

    @Override
    public CharBuffer slice(int index, int length) {
        Objects.checkFromIndexSize(index, length, limit());
        return new ByteBufferAsCharBufferL(bb,
                                                    -1,
                                                    0,
                                                    length,
                                                    length,
                                                    byteOffset(index), segment);
    }

    public CharBuffer duplicate() {
        return new ByteBufferAsCharBufferL(bb,
                                                    this.markValue(),
                                                    this.position(),
                                                    this.limit(),
                                                    this.capacity(),
                                                    address, segment);
    }

    public CharBuffer asReadOnlyBuffer() {

        return new ByteBufferAsCharBufferRL(bb,
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

    public char get() {
        char x = SCOPED_MEMORY_ACCESS.getCharUnaligned(session(), bb.hb, byteOffset(nextGetIndex()),
            false);
        return (x);
    }

    public char get(int i) {
        char x = SCOPED_MEMORY_ACCESS.getCharUnaligned(session(), bb.hb, byteOffset(checkIndex(i)),
            false);
        return (x);
    }


   char getUnchecked(int i) {
        char x = SCOPED_MEMORY_ACCESS.getCharUnaligned(null, bb.hb, byteOffset(i),
            false);
        return (x);
    }




    public CharBuffer put(char x) {

        char y = (x);
        SCOPED_MEMORY_ACCESS.putCharUnaligned(session(), bb.hb, byteOffset(nextPutIndex()), y,
            false);
        return this;



    }

    public CharBuffer put(int i, char x) {

        char y = (x);
        SCOPED_MEMORY_ACCESS.putCharUnaligned(session(), bb.hb, byteOffset(checkIndex(i)), y,
            false);
        return this;



    }

    public CharBuffer compact() {

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



    public String toString(int start, int end) {
        Objects.checkFromToIndex(start, end, limit());
        int len = end - start;
        char[] ca = new char[len];
        CharBuffer cb = CharBuffer.wrap(ca);
        CharBuffer db = this.duplicate();
        db.position(start);
        db.limit(end);
        cb.put(db);
        return new String(ca);
    }


    // --- Methods to support CharSequence ---

    public CharBuffer subSequence(int start, int end) {
        int pos = position();
        int lim = limit();
        assert (pos <= lim);
        pos = (pos <= lim ? pos : lim);
        int len = lim - pos;

        Objects.checkFromToIndex(start, end, len);
        return new ByteBufferAsCharBufferL(bb,
                                                  -1,
                                                  pos + start,
                                                  pos + end,
                                                  capacity(),
                                                  address, segment);
    }




    public ByteOrder order() {




        return ByteOrder.LITTLE_ENDIAN;

    }


    ByteOrder charRegionOrder() {
        return order();
    }

}
