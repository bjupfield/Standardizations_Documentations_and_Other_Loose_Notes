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



 * A read-only HeapByteBuffer.  This class extends the corresponding
 * read/write class, overriding the mutation methods to throw a {@link
 * ReadOnlyBufferException} and overriding the view-buffer methods to return an
 * instance of this class rather than of the superclass.

 */



final

class HeapByteBufferR
    extends HeapByteBuffer



{















    HeapByteBufferR(int cap, int lim, MemorySegment segment) {            // package-private








        super(cap, lim, segment);
        this.isReadOnly = true;

    }

    HeapByteBufferR(byte[] buf, int off, int len, MemorySegment segment) { // package-private








        super(buf, off, len, segment);
        this.isReadOnly = true;

    }

    protected HeapByteBufferR(byte[] buf,
                                   int mark, int pos, int lim, int cap,
                                   int off, MemorySegment segment)
    {








        super(buf, mark, pos, lim, cap, off, segment);
        this.isReadOnly = true;

    }

    public ByteBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        int rem = (pos <= lim ? lim - pos : 0);
        return new HeapByteBufferR(hb,
                                        -1,
                                        0,
                                        rem,
                                        rem,
                                        pos + offset, segment);
    }

    @Override
    public ByteBuffer slice(int index, int length) {
        Objects.checkFromIndexSize(index, length, limit());
        return new HeapByteBufferR(hb,
                                        -1,
                                        0,
                                        length,
                                        length,
                                        index + offset, segment);
    }

    public ByteBuffer duplicate() {
        return new HeapByteBufferR(hb,
                                        this.markValue(),
                                        this.position(),
                                        this.limit(),
                                        this.capacity(),
                                        offset, segment);
    }

    public ByteBuffer asReadOnlyBuffer() {








        return duplicate();

    }




















































    public boolean isReadOnly() {
        return true;
    }

    public ByteBuffer put(byte x) {




        throw new ReadOnlyBufferException();

    }

    public ByteBuffer put(int i, byte x) {




        throw new ReadOnlyBufferException();

    }

    public ByteBuffer put(byte[] src, int offset, int length) {










        throw new ReadOnlyBufferException();

    }

    public ByteBuffer put(ByteBuffer src) {





        throw new ReadOnlyBufferException();

    }

    public ByteBuffer put(int index, ByteBuffer src, int offset, int length) {





        throw new ReadOnlyBufferException();

    }

    public ByteBuffer put(int index, byte[] src, int offset, int length) {







        throw new ReadOnlyBufferException();

    }















































































    public ByteBuffer compact() {











        throw new ReadOnlyBufferException();

    }





    byte _get(int i) {                          // package-private
        return hb[i];
    }

    void _put(int i, byte b) {                  // package-private



        throw new ReadOnlyBufferException();

    }

    // char













    public ByteBuffer putChar(char x) {




        throw new ReadOnlyBufferException();

    }

    public ByteBuffer putChar(int i, char x) {




        throw new ReadOnlyBufferException();

    }

    public CharBuffer asCharBuffer() {
        int pos = position();
        int size = (limit() - pos) >> 1;
        long addr = address + pos;
        return (bigEndian
                ? (CharBuffer)(new ByteBufferAsCharBufferRB(this,
                                                               -1,
                                                               0,
                                                               size,
                                                               size,
                                                               addr, segment))
                : (CharBuffer)(new ByteBufferAsCharBufferRL(this,
                                                               -1,
                                                               0,
                                                               size,
                                                               size,
                                                               addr, segment)));
    }


    // short













    public ByteBuffer putShort(short x) {




        throw new ReadOnlyBufferException();

    }

    public ByteBuffer putShort(int i, short x) {




        throw new ReadOnlyBufferException();

    }

    public ShortBuffer asShortBuffer() {
        int pos = position();
        int size = (limit() - pos) >> 1;
        long addr = address + pos;
        return (bigEndian
                ? (ShortBuffer)(new ByteBufferAsShortBufferRB(this,
                                                                 -1,
                                                                 0,
                                                                 size,
                                                                 size,
                                                                 addr, segment))
                : (ShortBuffer)(new ByteBufferAsShortBufferRL(this,
                                                                 -1,
                                                                 0,
                                                                 size,
                                                                 size,
                                                                 addr, segment)));
    }


    // int













    public ByteBuffer putInt(int x) {




        throw new ReadOnlyBufferException();

    }

    public ByteBuffer putInt(int i, int x) {




        throw new ReadOnlyBufferException();

    }

    public IntBuffer asIntBuffer() {
        int pos = position();
        int size = (limit() - pos) >> 2;
        long addr = address + pos;
        return (bigEndian
                ? (IntBuffer)(new ByteBufferAsIntBufferRB(this,
                                                             -1,
                                                             0,
                                                             size,
                                                             size,
                                                             addr, segment))
                : (IntBuffer)(new ByteBufferAsIntBufferRL(this,
                                                             -1,
                                                             0,
                                                             size,
                                                             size,
                                                             addr, segment)));
    }


    // long













    public ByteBuffer putLong(long x) {




        throw new ReadOnlyBufferException();

    }

    public ByteBuffer putLong(int i, long x) {




        throw new ReadOnlyBufferException();

    }

    public LongBuffer asLongBuffer() {
        int pos = position();
        int size = (limit() - pos) >> 3;
        long addr = address + pos;
        return (bigEndian
                ? (LongBuffer)(new ByteBufferAsLongBufferRB(this,
                                                               -1,
                                                               0,
                                                               size,
                                                               size,
                                                               addr, segment))
                : (LongBuffer)(new ByteBufferAsLongBufferRL(this,
                                                               -1,
                                                               0,
                                                               size,
                                                               size,
                                                               addr, segment)));
    }


    // float















    public ByteBuffer putFloat(float x) {





        throw new ReadOnlyBufferException();

    }

    public ByteBuffer putFloat(int i, float x) {





        throw new ReadOnlyBufferException();

    }

    public FloatBuffer asFloatBuffer() {
        int pos = position();
        int size = (limit() - pos) >> 2;
        long addr = address + pos;
        return (bigEndian
                ? (FloatBuffer)(new ByteBufferAsFloatBufferRB(this,
                                                                 -1,
                                                                 0,
                                                                 size,
                                                                 size,
                                                                 addr, segment))
                : (FloatBuffer)(new ByteBufferAsFloatBufferRL(this,
                                                                 -1,
                                                                 0,
                                                                 size,
                                                                 size,
                                                                 addr, segment)));
    }


    // double















    public ByteBuffer putDouble(double x) {





        throw new ReadOnlyBufferException();

    }

    public ByteBuffer putDouble(int i, double x) {





        throw new ReadOnlyBufferException();

    }

    public DoubleBuffer asDoubleBuffer() {
        int pos = position();
        int size = (limit() - pos) >> 3;
        long addr = address + pos;
        return (bigEndian
                ? (DoubleBuffer)(new ByteBufferAsDoubleBufferRB(this,
                                                                   -1,
                                                                   0,
                                                                   size,
                                                                   size,
                                                                   addr, segment))
                : (DoubleBuffer)(new ByteBufferAsDoubleBufferRL(this,
                                                                   -1,
                                                                   0,
                                                                   size,
                                                                   size,
                                                                   addr, segment)));
    }












































}
