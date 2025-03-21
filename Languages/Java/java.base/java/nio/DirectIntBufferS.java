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

import java.io.FileDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.ref.Reference;
import java.util.Objects;
import jdk.internal.foreign.MemorySessionImpl;
import jdk.internal.misc.ScopedMemoryAccess.ScopedAccessError;
import jdk.internal.misc.VM;
import jdk.internal.ref.Cleaner;
import sun.nio.ch.DirectBuffer;



sealed



class DirectIntBufferS

    extends IntBuffer



    implements DirectBuffer

    permits DirectIntBufferRS

{



    // Cached unaligned-access capability
    protected static final boolean UNALIGNED = Bits.unaligned();

    // Base address, used in all indexing calculations
    // NOTE: moved up to Buffer.java for speed in JNI GetDirectBufferAddress
    //    protected long address;

    // An object attached to this buffer. If this buffer is a view of another
    // buffer then we use this field to keep a reference to that buffer to
    // ensure that its memory isn't freed before we are done with it.
    private final Object att;

    public Object attachment() {
        return att;
    }




















    public Cleaner cleaner() { return null; }




















































































































    // For duplicates and slices
    //
    DirectIntBufferS(DirectBuffer db,         // package-private
                               int mark, int pos, int lim, int cap, int off,



                               MemorySegment segment)
    {

        super(mark, pos, lim, cap,



              segment);
        address = ((Buffer)db).address + off;



        Object attachment = db.attachment();
        att = (attachment == null ? db : attachment);








    }

    @Override
    Object base() {
        return null;
    }

    public IntBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        int rem = (pos <= lim ? lim - pos : 0);
        int off = (pos << 2);
        assert (off >= 0);
        return new DirectIntBufferS(this,
                                              -1,
                                              0,
                                              rem,
                                              rem,
                                              off,




                                              segment);
    }

    @Override
    public IntBuffer slice(int index, int length) {
        Objects.checkFromIndexSize(index, length, limit());
        return new DirectIntBufferS(this,
                                              -1,
                                              0,
                                              length,
                                              length,
                                              index << 2,




                                              segment);
    }

    public IntBuffer duplicate() {
        return new DirectIntBufferS(this,
                                              this.markValue(),
                                              this.position(),
                                              this.limit(),
                                              this.capacity(),
                                              0,




                                              segment);
    }

    public IntBuffer asReadOnlyBuffer() {

        return new DirectIntBufferRS(this,
                                           this.markValue(),
                                           this.position(),
                                           this.limit(),
                                           this.capacity(),
                                           0,




                                           segment);



    }



    public long address() {
        MemorySessionImpl session = session();
        if (session != null) {
            if (session.ownerThread() == null && session.isCloseable()) {
                throw new UnsupportedOperationException("ByteBuffer derived from closeable shared sessions not supported");
            }
            session.checkValidState();
        }
        return address;
    }

    private long ix(int i) {
        return address + ((long)i << 2);
    }

    public int get() {
        try {
            return (Bits.swap(SCOPED_MEMORY_ACCESS.getInt(session(), null, ix(nextGetIndex()))));
        } finally {
            Reference.reachabilityFence(this);
        }
    }

    public int get(int i) {
        try {
            return (Bits.swap(SCOPED_MEMORY_ACCESS.getInt(session(), null, ix(checkIndex(i)))));
        } finally {
            Reference.reachabilityFence(this);
        }
    }












    public IntBuffer put(int x) {

        try {
            SCOPED_MEMORY_ACCESS.putInt(session(), null, ix(nextPutIndex()), Bits.swap((x)));
        } finally {
            Reference.reachabilityFence(this);
        }
        return this;



    }

    public IntBuffer put(int i, int x) {

        try {
            SCOPED_MEMORY_ACCESS.putInt(session(), null, ix(checkIndex(i)), Bits.swap((x)));
        } finally {
            Reference.reachabilityFence(this);
        }
        return this;



    }

    public IntBuffer compact() {

        int pos = position();
        int lim = limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        try {
            // null is passed as destination MemorySession to avoid checking session() twice
            SCOPED_MEMORY_ACCESS.copyMemory(session(), null, null,
                    ix(pos), null, ix(0), (long)rem << 2);
        } finally {
            Reference.reachabilityFence(this);
        }
        position(rem);
        limit(capacity());
        discardMark();
        return this;



    }

    public boolean isDirect() {
        return true;
    }

    public boolean isReadOnly() {
        return false;
    }








































































































    public ByteOrder order() {

        return ((ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
                ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);





    }


















}
