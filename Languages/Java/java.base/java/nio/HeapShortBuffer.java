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

/**

 * A read/write HeapShortBuffer.






 */

sealed



class HeapShortBuffer
    extends ShortBuffer

    permits HeapShortBufferR

{

    // Cached array base offset
    private static final long ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(short[].class);

    // Cached array index scale
    private static final long ARRAY_INDEX_SCALE = UNSAFE.arrayIndexScale(short[].class);

    // For speed these fields are actually declared in X-Buffer;
    // these declarations are here as documentation
    /*
    protected final short[] hb;
    protected final int offset;
    */


    HeapShortBuffer(int cap, int lim, MemorySegment segment) {            // package-private

        super(-1, 0, lim, cap, new short[cap], 0, segment);
        /*
        hb = new short[cap];
        offset = 0;
        */
        this.address = ARRAY_BASE_OFFSET;




    }

    HeapShortBuffer(short[] buf, int off, int len, MemorySegment segment) { // package-private

        super(-1, off, off + len, buf.length, buf, 0, segment);
        /*
        hb = buf;
        offset = 0;
        */
        this.address = ARRAY_BASE_OFFSET;




    }

    protected HeapShortBuffer(short[] buf,
                                   int mark, int pos, int lim, int cap,
                                   int off, MemorySegment segment)
    {

        super(mark, pos, lim, cap, buf, off, segment);
        /*
        hb = buf;
        offset = off;
        */
        this.address = ARRAY_BASE_OFFSET + off * ARRAY_INDEX_SCALE;




    }

    public ShortBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        int rem = (pos <= lim ? lim - pos : 0);
        return new HeapShortBuffer(hb,
                                        -1,
                                        0,
                                        rem,
                                        rem,
                                        pos + offset, segment);
    }

    @Override
    public ShortBuffer slice(int index, int length) {
        Objects.checkFromIndexSize(index, length, limit());
        return new HeapShortBuffer(hb,
                                        -1,
                                        0,
                                        length,
                                        length,
                                        index + offset, segment);
    }

    public ShortBuffer duplicate() {
        return new HeapShortBuffer(hb,
                                        this.markValue(),
                                        this.position(),
                                        this.limit(),
                                        this.capacity(),
                                        offset, segment);
    }

    public ShortBuffer asReadOnlyBuffer() {

        return new HeapShortBufferR(hb,
                                     this.markValue(),
                                     this.position(),
                                     this.limit(),
                                     this.capacity(),
                                     offset, segment);



    }



    protected int ix(int i) {
        return i + offset;
    }







    public short get() {
        return hb[ix(nextGetIndex())];
    }

    public short get(int i) {
        return hb[ix(checkIndex(i))];
    }







    public ShortBuffer get(short[] dst, int offset, int length) {
        checkSession();
        Objects.checkFromIndexSize(offset, length, dst.length);
        int pos = position();
        if (length > limit() - pos)
            throw new BufferUnderflowException();
        System.arraycopy(hb, ix(pos), dst, offset, length);
        position(pos + length);
        return this;
    }

    public ShortBuffer get(int index, short[] dst, int offset, int length) {
        checkSession();
        Objects.checkFromIndexSize(index, length, limit());
        Objects.checkFromIndexSize(offset, length, dst.length);
        System.arraycopy(hb, ix(index), dst, offset, length);
        return this;
    }

    public boolean isDirect() {
        return false;
    }



    public boolean isReadOnly() {
        return false;
    }

    public ShortBuffer put(short x) {

        hb[ix(nextPutIndex())] = x;
        return this;



    }

    public ShortBuffer put(int i, short x) {

        hb[ix(checkIndex(i))] = x;
        return this;



    }

    public ShortBuffer put(short[] src, int offset, int length) {

        checkSession();
        Objects.checkFromIndexSize(offset, length, src.length);
        int pos = position();
        if (length > limit() - pos)
            throw new BufferOverflowException();
        System.arraycopy(src, offset, hb, ix(pos), length);
        position(pos + length);
        return this;



    }

    public ShortBuffer put(ShortBuffer src) {

        checkSession();
        super.put(src);
        return this;



    }

    public ShortBuffer put(int index, ShortBuffer src, int offset, int length) {

        checkSession();
        super.put(index, src, offset, length);
        return this;



    }

    public ShortBuffer put(int index, short[] src, int offset, int length) {

        checkSession();
        Objects.checkFromIndexSize(index, length, limit());
        Objects.checkFromIndexSize(offset, length, src.length);
        System.arraycopy(src, offset, hb, ix(index), length);
        return this;



    }















































































    public ShortBuffer compact() {

        int pos = position();
        int lim = limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        System.arraycopy(hb, ix(pos), hb, ix(0), rem);
        position(rem);
        limit(capacity());
        discardMark();
        return this;



    }

















































































































































































































































































































































































    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }







}
