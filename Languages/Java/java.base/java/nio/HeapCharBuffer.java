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

 * A read/write HeapCharBuffer.






 */

sealed



class HeapCharBuffer
    extends CharBuffer

    permits HeapCharBufferR

{

    // Cached array base offset
    private static final long ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(char[].class);

    // Cached array index scale
    private static final long ARRAY_INDEX_SCALE = UNSAFE.arrayIndexScale(char[].class);

    // For speed these fields are actually declared in X-Buffer;
    // these declarations are here as documentation
    /*
    protected final char[] hb;
    protected final int offset;
    */


    HeapCharBuffer(int cap, int lim, MemorySegment segment) {            // package-private

        super(-1, 0, lim, cap, new char[cap], 0, segment);
        /*
        hb = new char[cap];
        offset = 0;
        */
        this.address = ARRAY_BASE_OFFSET;




    }

    HeapCharBuffer(char[] buf, int off, int len, MemorySegment segment) { // package-private

        super(-1, off, off + len, buf.length, buf, 0, segment);
        /*
        hb = buf;
        offset = 0;
        */
        this.address = ARRAY_BASE_OFFSET;




    }

    protected HeapCharBuffer(char[] buf,
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

    public CharBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        int rem = (pos <= lim ? lim - pos : 0);
        return new HeapCharBuffer(hb,
                                        -1,
                                        0,
                                        rem,
                                        rem,
                                        pos + offset, segment);
    }

    @Override
    public CharBuffer slice(int index, int length) {
        Objects.checkFromIndexSize(index, length, limit());
        return new HeapCharBuffer(hb,
                                        -1,
                                        0,
                                        length,
                                        length,
                                        index + offset, segment);
    }

    public CharBuffer duplicate() {
        return new HeapCharBuffer(hb,
                                        this.markValue(),
                                        this.position(),
                                        this.limit(),
                                        this.capacity(),
                                        offset, segment);
    }

    public CharBuffer asReadOnlyBuffer() {

        return new HeapCharBufferR(hb,
                                     this.markValue(),
                                     this.position(),
                                     this.limit(),
                                     this.capacity(),
                                     offset, segment);



    }



    protected int ix(int i) {
        return i + offset;
    }







    public char get() {
        return hb[ix(nextGetIndex())];
    }

    public char get(int i) {
        return hb[ix(checkIndex(i))];
    }


    char getUnchecked(int i) {
    return hb[ix(i)];
    }


    public CharBuffer get(char[] dst, int offset, int length) {
        checkSession();
        Objects.checkFromIndexSize(offset, length, dst.length);
        int pos = position();
        if (length > limit() - pos)
            throw new BufferUnderflowException();
        System.arraycopy(hb, ix(pos), dst, offset, length);
        position(pos + length);
        return this;
    }

    public CharBuffer get(int index, char[] dst, int offset, int length) {
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

    public CharBuffer put(char x) {

        hb[ix(nextPutIndex())] = x;
        return this;



    }

    public CharBuffer put(int i, char x) {

        hb[ix(checkIndex(i))] = x;
        return this;



    }

    public CharBuffer put(char[] src, int offset, int length) {

        checkSession();
        Objects.checkFromIndexSize(offset, length, src.length);
        int pos = position();
        if (length > limit() - pos)
            throw new BufferOverflowException();
        System.arraycopy(src, offset, hb, ix(pos), length);
        position(pos + length);
        return this;



    }

    public CharBuffer put(CharBuffer src) {

        checkSession();
        super.put(src);
        return this;



    }

    public CharBuffer put(int index, CharBuffer src, int offset, int length) {

        checkSession();
        super.put(index, src, offset, length);
        return this;



    }

    public CharBuffer put(int index, char[] src, int offset, int length) {

        checkSession();
        Objects.checkFromIndexSize(index, length, limit());
        Objects.checkFromIndexSize(offset, length, src.length);
        System.arraycopy(src, offset, hb, ix(index), length);
        return this;



    }



    //
    // Use getChars() to load chars directly into the heap buffer array.
    // For a String or StringBuffer source this improves performance if
    // a proper subsequence is being appended as copying to a new intermediate
    // String object is avoided. For a StringBuilder where either a subsequence
    // or the full sequence of chars is being appended, copying the chars to
    // an intermedite String in StringBuilder::toString is avoided.
    //
    private CharBuffer appendChars(CharSequence csq, int start, int end) {
        checkSession();

        Objects.checkFromToIndex(start, end, csq.length());

        int length = end - start;
        int pos = position();
        int lim = limit();
        int rem = (pos <= lim) ? lim - pos : 0;
        if (length > rem)
            throw new BufferOverflowException();

        if (csq instanceof String str) {
            str.getChars(start, end, hb, ix(pos));
        } else if (csq instanceof StringBuilder sb) {
            sb.getChars(start, end, hb, ix(pos));
        } else if (csq instanceof StringBuffer sb) {
            sb.getChars(start, end, hb, ix(pos));
        }

        position(pos + length);

        return this;
    }

    public CharBuffer append(CharSequence csq) {

        if (csq instanceof StringBuilder)
            return appendChars(csq, 0, csq.length());

        return super.append(csq);



    }

    public CharBuffer append(CharSequence csq, int start, int end) {

        if (csq instanceof String || csq instanceof StringBuffer ||
            csq instanceof StringBuilder)
            return appendChars(csq, start, end);

        return super.append(csq, start, end);



    }

    public CharBuffer put(String src, int start, int end) {

        checkSession();
        int length = end - start;
        Objects.checkFromIndexSize(start, length, src.length());
        int pos = position();
        int lim = limit();
        int rem = (pos <= lim) ? lim - pos : 0;
        if (length > rem)
            throw new BufferOverflowException();
        src.getChars(start, end, hb, ix(pos));
        position(pos + length);
        return this;



    }



    public CharBuffer compact() {

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






















































































































































































































































































































































    String toString(int start, int end) {               // package-private
        try {
            return new String(hb, start + offset, end - start);
        } catch (StringIndexOutOfBoundsException x) {
            throw new IndexOutOfBoundsException();
        }
    }


    // --- Methods to support CharSequence ---

    public CharBuffer subSequence(int start, int end) {
        int pos = position();
        Objects.checkFromToIndex(start, end, limit() - pos);
        return new HeapCharBuffer(hb,
                                      -1,
                                      pos + start,
                                      pos + end,
                                      capacity(),
                                      offset, segment);
    }






    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }



    ByteOrder charRegionOrder() {
        return order();
    }

}
