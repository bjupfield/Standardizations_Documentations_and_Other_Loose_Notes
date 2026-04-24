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



 * A read-only HeapIntBuffer.  This class extends the corresponding
 * read/write class, overriding the mutation methods to throw a {@link
 * ReadOnlyBufferException} and overriding the view-buffer methods to return an
 * instance of this class rather than of the superclass.

 */



final

class HeapIntBufferR
    extends HeapIntBuffer



{















    HeapIntBufferR(int cap, int lim, MemorySegment segment) {            // package-private








        super(cap, lim, segment);
        this.isReadOnly = true;

    }

    HeapIntBufferR(int[] buf, int off, int len, MemorySegment segment) { // package-private








        super(buf, off, len, segment);
        this.isReadOnly = true;

    }

    protected HeapIntBufferR(int[] buf,
                                   int mark, int pos, int lim, int cap,
                                   int off, MemorySegment segment)
    {








        super(buf, mark, pos, lim, cap, off, segment);
        this.isReadOnly = true;

    }

    public IntBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        int rem = (pos <= lim ? lim - pos : 0);
        return new HeapIntBufferR(hb,
                                        -1,
                                        0,
                                        rem,
                                        rem,
                                        pos + offset, segment);
    }

    @Override
    public IntBuffer slice(int index, int length) {
        Objects.checkFromIndexSize(index, length, limit());
        return new HeapIntBufferR(hb,
                                        -1,
                                        0,
                                        length,
                                        length,
                                        index + offset, segment);
    }

    public IntBuffer duplicate() {
        return new HeapIntBufferR(hb,
                                        this.markValue(),
                                        this.position(),
                                        this.limit(),
                                        this.capacity(),
                                        offset, segment);
    }

    public IntBuffer asReadOnlyBuffer() {








        return duplicate();

    }




















































    public boolean isReadOnly() {
        return true;
    }

    public IntBuffer put(int x) {




        throw new ReadOnlyBufferException();

    }

    public IntBuffer put(int i, int x) {




        throw new ReadOnlyBufferException();

    }

    public IntBuffer put(int[] src, int offset, int length) {










        throw new ReadOnlyBufferException();

    }

    public IntBuffer put(IntBuffer src) {





        throw new ReadOnlyBufferException();

    }

    public IntBuffer put(int index, IntBuffer src, int offset, int length) {





        throw new ReadOnlyBufferException();

    }

    public IntBuffer put(int index, int[] src, int offset, int length) {







        throw new ReadOnlyBufferException();

    }















































































    public IntBuffer compact() {











        throw new ReadOnlyBufferException();

    }

















































































































































































































































































































































































    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }







}
