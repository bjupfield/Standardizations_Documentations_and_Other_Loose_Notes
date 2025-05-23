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




import java.lang.ref.Reference;






import java.lang.foreign.MemorySegment;
import java.util.Objects;
import jdk.internal.util.ArraysSupport;

/**
 * A float buffer.
 *
 * <p> This class defines four categories of operations upon
 * float buffers:
 *
 * <ul>
 *
 *   <li><p> Absolute and relative {@link #get() <i>get</i>} and
 *   {@link #put(float) <i>put</i>} methods that read and write
 *   single floats; </p></li>
 *
 *   <li><p> Absolute and relative {@link #get(float[]) <i>bulk get</i>}
 *   methods that transfer contiguous sequences of floats from this buffer
 *   into an array;</p></li>
 *
 *   <li><p> Absolute and relative {@link #put(float[]) <i>bulk put</i>}
 *   methods that transfer contiguous sequences of floats from a
 *   float array or some other float
 *   buffer into this buffer;</p></li>
 *












 *
 *   <li><p> A method for {@link #compact compacting}
 *   a float buffer.  </p></li>
 *
 * </ul>
 *
 * <p> Float buffers can be created either by {@link #allocate
 * <i>allocation</i>}, which allocates space for the buffer's
 *






 *
 * content, by {@link #wrap(float[]) <i>wrapping</i>} an existing
 * float array  into a buffer, or by creating a
 * <a href="ByteBuffer.html#views"><i>view</i></a> of an existing byte buffer.
 *

 *







































































































*

 *
 * <p> Like a byte buffer, a float buffer is either <a
 * href="ByteBuffer.html#direct"><i>direct</i> or <i>non-direct</i></a>.  A
 * float buffer created via the {@code wrap} methods of this class will
 * be non-direct.  A float buffer created as a view of a byte buffer will
 * be direct if, and only if, the byte buffer itself is direct.  Whether or not
 * a float buffer is direct may be determined by invoking the {@link
 * #isDirect isDirect} method.  </p>
 *

*










 *



 *
 * <p> Methods in this class that do not otherwise have a value to return are
 * specified to return the buffer upon which they are invoked.  This allows
 * method invocations to be chained.
 *



































 * <h2> Optional operations </h2>
 * Methods specified as
 * <i>{@linkplain Buffer##read-only-buffers-heading optional
 * operations}</i> throw a {@linkplain ReadOnlyBufferException} when invoked
 * on a {@linkplain java.nio.Buffer#isReadOnly read-only} FloatBuffer. The
 * methods {@linkplain #array array} and {@linkplain #arrayOffset arrayOffset}
 * throw an {@linkplain UnsupportedOperationException} if the FloatBuffer is
 * not backed by an {@linkplain Buffer#hasArray accessible float array}
 * (irrespective of whether the FloatBuffer is read-only).
 *
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @since 1.4
 */

public abstract sealed class FloatBuffer
    extends Buffer
    implements Comparable<FloatBuffer>
    permits






    HeapFloatBuffer, DirectFloatBufferS, DirectFloatBufferU,
    ByteBufferAsFloatBufferB, ByteBufferAsFloatBufferL

{
    // Cached array base offset
    private static final long ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(float[].class);

    // These fields are declared here rather than in Heap-X-Buffer in order to
    // reduce the number of virtual method invocations needed to access these
    // values, which is especially costly when coding small buffers.
    //
    final float[] hb;                  // Non-null only for heap buffers
    final int offset;
    boolean isReadOnly;

    // Creates a new buffer with the given mark, position, limit, capacity,
    // backing array, and array offset
    //
    FloatBuffer(int mark, int pos, int lim, int cap,   // package-private
                 float[] hb, int offset, MemorySegment segment)
    {
        super(mark, pos, lim, cap, segment);
        this.hb = hb;
        this.offset = offset;
    }

    // Creates a new buffer with the given mark, position, limit, and capacity
    //
    FloatBuffer(int mark, int pos, int lim, int cap, MemorySegment segment) { // package-private
        this(mark, pos, lim, cap, null, 0, segment);
    }

    // Creates a new buffer with given base, address and capacity
    //
    FloatBuffer(float[] hb, long addr, int cap, MemorySegment segment) { // package-private
        super(addr, cap, segment);
        this.hb = hb;
        this.offset = 0;
    }

    @Override
    Object base() {
        return hb;
    }


























    /**
     * Allocates a new float buffer.
     *
     * <p> The new buffer's position will be zero, its limit will be its
     * capacity, its mark will be undefined, each of its elements will be
     * initialized to zero, and its byte order will be



     * the {@link ByteOrder#nativeOrder native order} of the underlying
     * hardware.

     * It will have a {@link #array backing array}, and its
     * {@link #arrayOffset array offset} will be zero.
     *
     * @param  capacity
     *         The new buffer's capacity, in floats
     *
     * @return  The new float buffer
     *
     * @throws  IllegalArgumentException
     *          If the {@code capacity} is a negative integer
     */
    public static FloatBuffer allocate(int capacity) {
        if (capacity < 0)
            throw createCapacityException(capacity);
        return new HeapFloatBuffer(capacity, capacity, null);
    }

    /**
     * Wraps a float array into a buffer.
     *
     * <p> The new buffer will be backed by the given float array;
     * that is, modifications to the buffer will cause the array to be modified
     * and vice versa.  The new buffer's capacity will be
     * {@code array.length}, its position will be {@code offset}, its limit
     * will be {@code offset + length}, its mark will be undefined, and its
     * byte order will be



     * the {@link ByteOrder#nativeOrder native order} of the underlying
     * hardware.

     * Its {@link #array backing array} will be the given array, and
     * its {@link #arrayOffset array offset} will be zero.  </p>
     *
     * @param  array
     *         The array that will back the new buffer
     *
     * @param  offset
     *         The offset of the subarray to be used; must be non-negative and
     *         no larger than {@code array.length}.  The new buffer's position
     *         will be set to this value.
     *
     * @param  length
     *         The length of the subarray to be used;
     *         must be non-negative and no larger than
     *         {@code array.length - offset}.
     *         The new buffer's limit will be set to {@code offset + length}.
     *
     * @return  The new float buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the {@code offset} and {@code length}
     *          parameters do not hold
     */
    public static FloatBuffer wrap(float[] array,
                                    int offset, int length)
    {
        try {
            return new HeapFloatBuffer(array, offset, length, null);
        } catch (IllegalArgumentException x) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Wraps a float array into a buffer.
     *
     * <p> The new buffer will be backed by the given float array;
     * that is, modifications to the buffer will cause the array to be modified
     * and vice versa.  The new buffer's capacity and limit will be
     * {@code array.length}, its position will be zero, its mark will be
     * undefined, and its byte order will be



     * the {@link ByteOrder#nativeOrder native order} of the underlying
     * hardware.

     * Its {@link #array backing array} will be the given array, and its
     * {@link #arrayOffset array offset} will be zero.  </p>
     *
     * @param  array
     *         The array that will back this buffer
     *
     * @return  The new float buffer
     */
    public static FloatBuffer wrap(float[] array) {
        return wrap(array, 0, array.length);
    }





































































































    /**
     * Creates a new float buffer whose content is a shared subsequence of
     * this buffer's content.
     *
     * <p> The content of the new buffer will start at this buffer's current
     * position.  Changes to this buffer's content will be visible in the new
     * buffer, and vice versa; the two buffers' position, limit, and mark
     * values will be independent.
     *
     * <p> The new buffer's position will be zero, its capacity and its limit
     * will be the number of floats remaining in this buffer, its mark will be
     * undefined, and its byte order will be



     * identical to that of this buffer.

     * The new buffer will be direct if, and only if, this buffer is direct, and
     * it will be read-only if, and only if, this buffer is read-only.  </p>
     *
     * @return  The new float buffer




     */
    @Override
    public abstract FloatBuffer slice();

    /**
     * Creates a new float buffer whose content is a shared subsequence of
     * this buffer's content.
     *
     * <p> The content of the new buffer will start at position {@code index}
     * in this buffer, and will contain {@code length} elements. Changes to
     * this buffer's content will be visible in the new buffer, and vice versa;
     * the two buffers' position, limit, and mark values will be independent.
     *
     * <p> The new buffer's position will be zero, its capacity and its limit
     * will be {@code length}, its mark will be undefined, and its byte order
     * will be



     * identical to that of this buffer.

     * The new buffer will be direct if, and only if, this buffer is direct,
     * and it will be read-only if, and only if, this buffer is read-only. </p>
     *
     * @param   index
     *          The position in this buffer at which the content of the new
     *          buffer will start; must be non-negative and no larger than
     *          {@link #limit() limit()}
     *
     * @param   length
     *          The number of elements the new buffer will contain; must be
     *          non-negative and no larger than {@code limit() - index}
     *
     * @return  The new buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative or greater than {@code limit()},
     *          {@code length} is negative, or {@code length > limit() - index}
     *
     * @since 13
     */
    @Override
    public abstract FloatBuffer slice(int index, int length);

    /**
     * Creates a new float buffer that shares this buffer's content.
     *
     * <p> The content of the new buffer will be that of this buffer.  Changes
     * to this buffer's content will be visible in the new buffer, and vice
     * versa; the two buffers' position, limit, and mark values will be
     * independent.
     *
     * <p> The new buffer's capacity, limit, position,




     * mark values, and byte order will be identical to those of this buffer.

     * The new buffer will be direct if, and only if, this buffer is direct, and
     * it will be read-only if, and only if, this buffer is read-only.  </p>
     *
     * @return  The new float buffer
     */
    @Override
    public abstract FloatBuffer duplicate();

    /**
     * Creates a new, read-only float buffer that shares this buffer's
     * content.
     *
     * <p> The content of the new buffer will be that of this buffer.  Changes
     * to this buffer's content will be visible in the new buffer; the new
     * buffer itself, however, will be read-only and will not allow the shared
     * content to be modified.  The two buffers' position, limit, and mark
     * values will be independent.
     *
     * <p> The new buffer's capacity, limit, position,




     * mark values, and byte order will be identical to those of this buffer.

     *
     * <p> If this buffer is itself read-only then this method behaves in
     * exactly the same way as the {@link #duplicate duplicate} method.  </p>
     *
     * @return  The new, read-only float buffer
     */
    public abstract FloatBuffer asReadOnlyBuffer();


    // -- Singleton get/put methods --

    /**
     * Relative <i>get</i> method.  Reads the float at this buffer's
     * current position, and then increments the position.
     *
     * @return  The float at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If the buffer's current position is not smaller than its limit
     */
    public abstract float get();

    /**
     * Relative <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes the given float into this buffer at the current
     * position, and then increments the position. </p>
     *
     * @param  f
     *         The float to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If this buffer's current position is not smaller than its limit
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract FloatBuffer put(float f);

    /**
     * Absolute <i>get</i> method.  Reads the float at the given
     * index.
     *
     * @param  index
     *         The index from which the float will be read
     *
     * @return  The float at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the buffer's limit
     */
    public abstract float get(int index);














    /**
     * Absolute <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes the given float into this buffer at the given
     * index. </p>
     *
     * @param  index
     *         The index at which the float will be written
     *
     * @param  f
     *         The float value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the buffer's limit
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract FloatBuffer put(int index, float f);


    // -- Bulk get operations --

    /**
     * Relative bulk <i>get</i> method.
     *
     * <p> This method transfers floats from this buffer into the given
     * destination array.  If there are fewer floats remaining in the
     * buffer than are required to satisfy the request, that is, if
     * {@code length}&nbsp;{@code >}&nbsp;{@code remaining()}, then no
     * floats are transferred and a {@link BufferUnderflowException} is
     * thrown.
     *
     * <p> Otherwise, this method copies {@code length} floats from this
     * buffer into the given array, starting at the current position of this
     * buffer and at the given offset in the array.  The position of this
     * buffer is then incremented by {@code length}.
     *
     * <p> In other words, an invocation of this method of the form
     * <code>src.get(dst,&nbsp;off,&nbsp;len)</code> has exactly the same effect as
     * the loop
     *
     * {@snippet lang=java :
     *     for (int i = off; i < off + len; i++)
     *         dst[i] = src.get();
     * }
     *
     * except that it first checks that there are sufficient floats in
     * this buffer and it is potentially much more efficient.
     *
     * @param  dst
     *         The array into which floats are to be written
     *
     * @param  offset
     *         The offset within the array of the first float to be
     *         written; must be non-negative and no larger than
     *         {@code dst.length}
     *
     * @param  length
     *         The maximum number of floats to be written to the given
     *         array; must be non-negative and no larger than
     *         {@code dst.length - offset}
     *
     * @return  This buffer
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than {@code length} floats
     *          remaining in this buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the {@code offset} and {@code length}
     *          parameters do not hold
     */
    public FloatBuffer get(float[] dst, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, dst.length);
        int pos = position();
        if (length > limit() - pos)
            throw new BufferUnderflowException();

        getArray(pos, dst, offset, length);

        position(pos + length);
        return this;
    }

    /**
     * Relative bulk <i>get</i> method.
     *
     * <p> This method transfers floats from this buffer into the given
     * destination array.  An invocation of this method of the form
     * {@code src.get(a)} behaves in exactly the same way as the invocation
     *
     * {@snippet lang=java :
     *     src.get(a, 0, a.length)
     * }
     *
     * @param   dst
     *          The destination array
     *
     * @return  This buffer
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than {@code length} floats
     *          remaining in this buffer
     */
    public FloatBuffer get(float[] dst) {
        return get(dst, 0, dst.length);
    }

    /**
     * Absolute bulk <i>get</i> method.
     *
     * <p> This method transfers {@code length} floats from this
     * buffer into the given array, starting at the given index in this
     * buffer and at the given offset in the array.  The position of this
     * buffer is unchanged.
     *
     * <p> An invocation of this method of the form
     * <code>src.get(index,&nbsp;dst,&nbsp;offset,&nbsp;length)</code>
     * has exactly the same effect as the following loop except that it first
     * checks the consistency of the supplied parameters and it is potentially
     * much more efficient:
     *
     * {@snippet lang=java :
     *     for (int i = offset, j = index; i < offset + length; i++, j++)
     *         dst[i] = src.get(j);
     * }
     *
     * @param  index
     *         The index in this buffer from which the first float will be
     *         read; must be non-negative and less than {@code limit()}
     *
     * @param  dst
     *         The destination array
     *
     * @param  offset
     *         The offset within the array of the first float to be
     *         written; must be non-negative and less than
     *         {@code dst.length}
     *
     * @param  length
     *         The number of floats to be written to the given array;
     *         must be non-negative and no larger than the smaller of
     *         {@code limit() - index} and {@code dst.length - offset}
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the {@code index}, {@code offset}, and
     *          {@code length} parameters do not hold
     *
     * @since 13
     */
    public FloatBuffer get(int index, float[] dst, int offset, int length) {
        Objects.checkFromIndexSize(index, length, limit());
        Objects.checkFromIndexSize(offset, length, dst.length);

        getArray(index, dst, offset, length);

        return this;
    }

    /**
     * Absolute bulk <i>get</i> method.
     *
     * <p> This method transfers floats from this buffer into the given
     * destination array.  The position of this buffer is unchanged.  An
     * invocation of this method of the form
     * <code>src.get(index,&nbsp;dst)</code> behaves in exactly the same
     * way as the invocation:
     *
     * {@snippet lang=java :
     *     src.get(index, dst, 0, dst.length)
     * }
     *
     * @param  index
     *         The index in this buffer from which the first float will be
     *         read; must be non-negative and less than {@code limit()}
     *
     * @param  dst
     *         The destination array
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative, not smaller than {@code limit()},
     *          or {@code limit() - index < dst.length}
     *
     * @since 13
     */
    public FloatBuffer get(int index, float[] dst) {
        return get(index, dst, 0, dst.length);
    }

    private FloatBuffer getArray(int index, float[] dst, int offset, int length) {
        if (



            ((long)length << 2) > Bits.JNI_COPY_TO_ARRAY_THRESHOLD) {
            long bufAddr = address + ((long)index << 2);
            long dstOffset =
                ARRAY_BASE_OFFSET + ((long)offset << 2);
            long len = (long)length << 2;

            try {

                if (order() != ByteOrder.nativeOrder())
                    SCOPED_MEMORY_ACCESS.copySwapMemory(
                            session(), null, base(), bufAddr,
                            dst, dstOffset, len, Float.BYTES);
                else

                    SCOPED_MEMORY_ACCESS.copyMemory(
                            session(), null, base(), bufAddr,
                            dst, dstOffset, len);
            } finally {
                Reference.reachabilityFence(this);
            }
        } else {
            int end = offset + length;
            for (int i = offset, j = index; i < end; i++, j++) {
                dst[i] = get(j);
            }
        }
        return this;
    }

    // -- Bulk put operations --

    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers the floats remaining in the given source
     * buffer into this buffer.  If there are more floats remaining in the
     * source buffer than in this buffer, that is, if
     * {@code src.remaining()}&nbsp;{@code >}&nbsp;{@code remaining()},
     * then no floats are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <i>n</i>&nbsp;=&nbsp;{@code src.remaining()} floats from the given
     * buffer into this buffer, starting at each buffer's current position.
     * The positions of both buffers are then incremented by <i>n</i>.
     *
     * <p> In other words, an invocation of this method of the form
     * {@code dst.put(src)} has exactly the same effect as the loop
     *
     * {@snippet lang=java :
     *     while (src.hasRemaining())
     *         dst.put(src.get());
     * }
     *
     * except that it first checks that there is sufficient space in this
     * buffer and it is potentially much more efficient.  If this buffer and
     * the source buffer share the same backing array or memory, then the
     * result will be as if the source elements were first copied to an
     * intermediate location before being written into this buffer.
     *
     * @param  src
     *         The source buffer from which floats are to be read;
     *         must not be this buffer
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the remaining floats in the source buffer
     *
     * @throws  IllegalArgumentException
     *          If the source buffer is this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public FloatBuffer put(FloatBuffer src) {
        if (src == this)
            throw createSameBufferException();
        if (isReadOnly())
            throw new ReadOnlyBufferException();

        int srcPos = src.position();
        int srcLim = src.limit();
        int srcRem = (srcPos <= srcLim ? srcLim - srcPos : 0);
        int pos = position();
        int lim = limit();
        int rem = (pos <= lim ? lim - pos : 0);

        if (srcRem > rem)
            throw new BufferOverflowException();

        putBuffer(pos, src, srcPos, srcRem);

        position(pos + srcRem);
        src.position(srcPos + srcRem);

        return this;
    }

    /**
     * Absolute bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers {@code length} floats into this buffer from
     * the given source buffer, starting at the given {@code offset} in the
     * source buffer and the given {@code index} in this buffer. The positions
     * of both buffers are unchanged.
     *
     * <p> In other words, an invocation of this method of the form
     * <code>dst.put(index,&nbsp;src,&nbsp;offset,&nbsp;length)</code>
     * has exactly the same effect as the loop
     *
     * {@snippet lang=java :
     *     for (int i = offset, j = index; i < offset + length; i++, j++)
     *         dst.put(j, src.get(i));
     * }
     *
     * except that it first checks the consistency of the supplied parameters
     * and it is potentially much more efficient.  If this buffer and
     * the source buffer share the same backing array or memory, then the
     * result will be as if the source elements were first copied to an
     * intermediate location before being written into this buffer.
     *
     * @param index
     *        The index in this buffer at which the first float will be
     *        written; must be non-negative and less than {@code limit()}
     *
     * @param src
     *        The buffer from which floats are to be read
     *
     * @param offset
     *        The index within the source buffer of the first float to be
     *        read; must be non-negative and less than {@code src.limit()}
     *
     * @param length
     *        The number of floats to be read from the given buffer;
     *        must be non-negative and no larger than the smaller of
     *        {@code limit() - index} and {@code src.limit() - offset}
     *
     * @return This buffer
     *
     * @throws IndexOutOfBoundsException
     *         If the preconditions on the {@code index}, {@code offset}, and
     *         {@code length} parameters do not hold
     *
     * @throws ReadOnlyBufferException
     *         If this buffer is read-only
     *
     * @since 16
     */
    public FloatBuffer put(int index, FloatBuffer src, int offset, int length) {
        Objects.checkFromIndexSize(index, length, limit());
        Objects.checkFromIndexSize(offset, length, src.limit());
        if (isReadOnly())
            throw new ReadOnlyBufferException();

        putBuffer(index, src, offset, length);

        return this;
    }

    void putBuffer(int pos, FloatBuffer src, int srcPos, int n) {

        Object srcBase = src.base();



        assert srcBase != null || src.isDirect();


            Object base = base();
            assert base != null || isDirect();

            long srcAddr = src.address + ((long)srcPos << 2);
            long addr = address + ((long)pos << 2);
            long len = (long)n << 2;

            try {

                if (this.order() != src.order())
                    SCOPED_MEMORY_ACCESS.copySwapMemory(
                            src.session(), session(), srcBase, srcAddr,
                            base, addr, len, Float.BYTES);
                else

                    SCOPED_MEMORY_ACCESS.copyMemory(
                            src.session(), session(), srcBase, srcAddr,
                            base, addr, len);
            } finally {
                Reference.reachabilityFence(src);
                Reference.reachabilityFence(this);
            }











    }

    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers floats into this buffer from the given
     * source array.  If there are more floats to be copied from the array
     * than remain in this buffer, that is, if
     * {@code length}&nbsp;{@code >}&nbsp;{@code remaining()}, then no
     * floats are transferred and a {@link BufferOverflowException} is
     * thrown.
     *
     * <p> Otherwise, this method copies {@code length} floats from the
     * given array into this buffer, starting at the given offset in the array
     * and at the current position of this buffer.  The position of this buffer
     * is then incremented by {@code length}.
     *
     * <p> In other words, an invocation of this method of the form
     * <code>dst.put(src,&nbsp;off,&nbsp;len)</code> has exactly the same effect as
     * the loop
     *
     * {@snippet lang=java :
     *     for (int i = off; i < off + len; i++)
     *         dst.put(src[i]);
     * }
     *
     * except that it first checks that there is sufficient space in this
     * buffer and it is potentially much more efficient.
     *
     * @param  src
     *         The array from which floats are to be read
     *
     * @param  offset
     *         The offset within the array of the first float to be read;
     *         must be non-negative and no larger than {@code src.length}
     *
     * @param  length
     *         The number of floats to be read from the given array;
     *         must be non-negative and no larger than
     *         {@code src.length - offset}
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the {@code offset} and {@code length}
     *          parameters do not hold
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public FloatBuffer put(float[] src, int offset, int length) {
        if (isReadOnly())
            throw new ReadOnlyBufferException();
        Objects.checkFromIndexSize(offset, length, src.length);
        int pos = position();
        if (length > limit() - pos)
            throw new BufferOverflowException();

        putArray(pos, src, offset, length);

        position(pos + length);
        return this;
    }

    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers the entire content of the given source
     * float array into this buffer.  An invocation of this method of the
     * form {@code dst.put(a)} behaves in exactly the same way as the
     * invocation
     *
     * {@snippet lang=java :
     *     dst.put(a, 0, a.length)
     * }
     *
     * @param   src
     *          The source array
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public final FloatBuffer put(float[] src) {
        return put(src, 0, src.length);
    }

    /**
     * Absolute bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers {@code length} floats from the given
     * array, starting at the given offset in the array and at the given index
     * in this buffer.  The position of this buffer is unchanged.
     *
     * <p> An invocation of this method of the form
     * <code>dst.put(index,&nbsp;src,&nbsp;offset,&nbsp;length)</code>
     * has exactly the same effect as the following loop except that it first
     * checks the consistency of the supplied parameters and it is potentially
     * much more efficient:
     *
     * {@snippet lang=java :
     *     for (int i = offset, j = index; i < offset + length; i++, j++)
     *         dst.put(j, src[i]);
     * }
     *
     * @param  index
     *         The index in this buffer at which the first float will be
     *         written; must be non-negative and less than {@code limit()}
     *
     * @param  src
     *         The array from which floats are to be read
     *
     * @param  offset
     *         The offset within the array of the first float to be read;
     *         must be non-negative and less than {@code src.length}
     *
     * @param  length
     *         The number of floats to be read from the given array;
     *         must be non-negative and no larger than the smaller of
     *         {@code limit() - index} and {@code src.length - offset}
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the {@code index}, {@code offset}, and
     *          {@code length} parameters do not hold
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     *
     * @since 13
     */
    public FloatBuffer put(int index, float[] src, int offset, int length) {
        if (isReadOnly())
            throw new ReadOnlyBufferException();
        Objects.checkFromIndexSize(index, length, limit());
        Objects.checkFromIndexSize(offset, length, src.length);

        putArray(index, src, offset, length);

        return this;
    }

    /**
     * Absolute bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method copies floats into this buffer from the given source
     * array.  The position of this buffer is unchanged.  An invocation of this
     * method of the form <code>dst.put(index,&nbsp;src)</code>
     * behaves in exactly the same way as the invocation:
     *
     * {@snippet lang=java :
     *     dst.put(index, src, 0, src.length);
     * }
     *
     * @param  index
     *         The index in this buffer at which the first float will be
     *         written; must be non-negative and less than {@code limit()}
     *
     * @param  src
     *         The array from which floats are to be read
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative, not smaller than {@code limit()},
     *          or {@code limit() - index < src.length}
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     *
     * @since 13
     */
    public FloatBuffer put(int index, float[] src) {
        return put(index, src, 0, src.length);
    }

    FloatBuffer putArray(int index, float[] src, int offset, int length) {

        if (



            ((long)length << 2) > Bits.JNI_COPY_FROM_ARRAY_THRESHOLD) {
            long bufAddr = address + ((long)index << 2);
            long srcOffset =
                ARRAY_BASE_OFFSET + ((long)offset << 2);
            long len = (long)length << 2;

            try {

                if (order() != ByteOrder.nativeOrder())
                    SCOPED_MEMORY_ACCESS.copySwapMemory(
                            null, session(), src, srcOffset,
                            base(), bufAddr, len, Float.BYTES);
                else

                    SCOPED_MEMORY_ACCESS.copyMemory(
                            null, session(), src, srcOffset,
                            base(), bufAddr, len);
            } finally {
                Reference.reachabilityFence(this);
            }
        } else {
            int end = offset + length;
            for (int i = offset, j = index; i < end; i++, j++)
                this.put(j, src[i]);
        }
        return this;



    }
































































































    // -- Other stuff --

    /**
     * Tells whether or not this buffer is backed by an accessible float
     * array.
     *
     * <p> If this method returns {@code true} then the {@link #array() array}
     * and {@link #arrayOffset() arrayOffset} methods may safely be invoked.
     * </p>
     *
     * @return  {@code true} if, and only if, this buffer
     *          is backed by an array and is not read-only
     */
    public final boolean hasArray() {
        return (hb != null) && !isReadOnly;
    }

    /**
     * Returns the float array that backs this
     * buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Modifications to this buffer's content will cause the returned
     * array's content to be modified, and vice versa.
     *
     * <p> Invoke the {@link #hasArray hasArray} method before invoking this
     * method in order to ensure that this buffer has an accessible backing
     * array.  </p>
     *
     * @return  The array that backs this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is backed by an array but is read-only
     *
     * @throws  UnsupportedOperationException
     *          If this buffer is not backed by an accessible array
     */
    public final float[] array() {
        if (hb == null)
            throw new UnsupportedOperationException();
        if (isReadOnly)
            throw new ReadOnlyBufferException();
        return hb;
    }

    /**
     * Returns the offset within this buffer's backing array of the first
     * element of the buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> If this buffer is backed by an array then buffer position <i>p</i>
     * corresponds to array index <i>p</i>&nbsp;+&nbsp;{@code arrayOffset()}.
     *
     * <p> Invoke the {@link #hasArray hasArray} method before invoking this
     * method in order to ensure that this buffer has an accessible backing
     * array.  </p>
     *
     * @return  The offset within this buffer's array
     *          of the first element of the buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is backed by an array but is read-only
     *
     * @throws  UnsupportedOperationException
     *          If this buffer is not backed by an accessible array
     */
    public final int arrayOffset() {
        if (hb == null)
            throw new UnsupportedOperationException();
        if (isReadOnly)
            throw new ReadOnlyBufferException();
        return offset;
    }

    // -- Covariant return type overrides

    /**
     * {@inheritDoc}
     * @since 9
     */
    @Override
    public

    final

    FloatBuffer position(int newPosition) {
        super.position(newPosition);
        return this;
    }
    
    /**
     * {@inheritDoc}
     * @since 9
     */
    @Override
    public

    final

    FloatBuffer limit(int newLimit) {
        super.limit(newLimit);
        return this;
    }
    
    /**
     * {@inheritDoc}
     * @since 9
     */
    @Override
    public 

    final

    FloatBuffer mark() {
        super.mark();
        return this;
    }

    /**
     * {@inheritDoc}
     * @since 9
     */
    @Override
    public 

    final

    FloatBuffer reset() {
        super.reset();
        return this;
    }

    /**
     * {@inheritDoc}
     * @since 9
     */
    @Override
    public 

    final

    FloatBuffer clear() {
        super.clear();
        return this;
    }

    /**
     * {@inheritDoc}
     * @since 9
     */
    @Override
    public 

    final

    FloatBuffer flip() {
        super.flip();
        return this;
    }

    /**
     * {@inheritDoc}
     * @since 9
     */
    @Override
    public 

    final

    FloatBuffer rewind() {
        super.rewind();
        return this;
    }

    /**
     * Compacts this buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> The floats between the buffer's current position and its limit,
     * if any, are copied to the beginning of the buffer.  That is, the
     * float at index <i>p</i>&nbsp;=&nbsp;{@code position()} is copied
     * to index zero, the float at index <i>p</i>&nbsp;+&nbsp;1 is copied
     * to index one, and so forth until the float at index
     * {@code limit()}&nbsp;-&nbsp;1 is copied to index
     * <i>n</i>&nbsp;=&nbsp;{@code limit()}&nbsp;-&nbsp;{@code 1}&nbsp;-&nbsp;<i>p</i>.
     * The buffer's position is then set to <i>n+1</i> and its limit is set to
     * its capacity.  The mark, if defined, is discarded.
     *
     * <p> The buffer's position is set to the number of floats copied,
     * rather than to zero, so that an invocation of this method can be
     * followed immediately by an invocation of another relative <i>put</i>
     * method. </p>
     *
















     *
     * @return  This buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract FloatBuffer compact();

    /**
     * Tells whether or not this float buffer is direct.
     *
     * @return  {@code true} if, and only if, this buffer is direct
     */
    public abstract boolean isDirect();

















    /**
     * Returns a string summarizing the state of this buffer.
     *
     * @return  A summary string
     */
    public String toString() {
        return getClass().getName()
                 + "[pos=" + position()
                 + " lim=" + limit()
                 + " cap=" + capacity()
                 + "]";
    }






    /**
     * Returns the current hash code of this buffer.
     *
     * <p> The hash code of a float buffer depends only upon its remaining
     * elements; that is, upon the elements from {@code position()} up to, and
     * including, the element at {@code limit()}&nbsp;-&nbsp;{@code 1}.
     *
     * <p> Because buffer hash codes are content-dependent, it is inadvisable
     * to use buffers as keys in hash maps or similar data structures unless it
     * is known that their contents will not change.  </p>
     *
     * @return  The current hash code of this buffer
     */
    public int hashCode() {
        int h = 1;
        int p = position();
        for (int i = limit() - 1; i >= p; i--)



            h = 31 * h + (int)get(i);

        return h;
    }

    /**
     * Tells whether or not this buffer is equal to another object.
     *
     * <p> Two float buffers are equal if, and only if,
     *
     * <ol>
     *
     *   <li><p> They have the same element type,  </p></li>
     *
     *   <li><p> They have the same number of remaining elements, and
     *   </p></li>
     *
     *   <li><p> The two sequences of remaining elements, considered
     *   independently of their starting positions, are pointwise equal.

     *   This method considers two float elements {@code a} and {@code b}
     *   to be equal if
     *   {@code (a == b) || (Float.isNaN(a) && Float.isNaN(b))}.
     *   The values {@code -0.0} and {@code +0.0} are considered to be
     *   equal, unlike {@link Float#equals(Object)}.

     *   </p></li>
     *
     * </ol>
     *
     * <p> A float buffer is not equal to any other type of object.  </p>
     *
     * @param  ob  The object to which this buffer is to be compared
     *
     * @return  {@code true} if, and only if, this buffer is equal to the
     *           given object
     */
    public boolean equals(Object ob) {
        if (this == ob)
            return true;
        if (!(ob instanceof FloatBuffer))
            return false;
        FloatBuffer that = (FloatBuffer)ob;
        int thisPos = this.position();
        int thisRem = this.limit() - thisPos;
        int thatPos = that.position();
        int thatRem = that.limit() - thatPos;
        if (thisRem < 0 || thisRem != thatRem)
            return false;
        return BufferMismatch.mismatch(this, thisPos,
                                       that, thatPos,
                                       thisRem) < 0;
    }

    /**
     * Compares this buffer to another.
     *
     * <p> Two float buffers are compared by comparing their sequences of
     * remaining elements lexicographically, without regard to the starting
     * position of each sequence within its corresponding buffer.

     * Pairs of {@code float} elements are compared as if by invoking
     * {@link Float#compare(float,float)}, except that
     * {@code -0.0} and {@code 0.0} are considered to be equal.
     * {@code Float.NaN} is considered by this method to be equal
     * to itself and greater than all other {@code float} values
     * (including {@code Float.POSITIVE_INFINITY}).




     *
     * <p> A float buffer is not comparable to any other type of object.
     *
     * @return  A negative integer, zero, or a positive integer as this buffer
     *          is less than, equal to, or greater than the given buffer
     */
    public int compareTo(FloatBuffer that) {
        int thisPos = this.position();
        int thisRem = this.limit() - thisPos;
        int thatPos = that.position();
        int thatRem = that.limit() - thatPos;
        int length = Math.min(thisRem, thatRem);
        if (length < 0)
            return -1;
        int i = BufferMismatch.mismatch(this, thisPos,
                                        that, thatPos,
                                        length);
        if (i >= 0) {
            return compare(this.get(thisPos + i), that.get(thatPos + i));
        }
        return thisRem - thatRem;
    }

    private static int compare(float x, float y) {

        return ((x < y)  ? -1 :
                (x > y)  ? +1 :
                (x == y) ?  0 :
                Float.isNaN(x) ? (Float.isNaN(y) ? 0 : +1) : -1);



    }

    /**
     * Finds and returns the relative index of the first mismatch between this
     * buffer and a given buffer.  The index is relative to the
     * {@link #position() position} of each buffer and will be in the range of
     * 0 (inclusive) up to the smaller of the {@link #remaining() remaining}
     * elements in each buffer (exclusive).
     *
     * <p> If the two buffers share a common prefix then the returned index is
     * the length of the common prefix and it follows that there is a mismatch
     * between the two buffers at that index within the respective buffers.
     * If one buffer is a proper prefix of the other then the returned index is
     * the smaller of the remaining elements in each buffer, and it follows that
     * the index is only valid for the buffer with the larger number of
     * remaining elements.
     * Otherwise, there is no mismatch.
     *
     * @param  that
     *         The byte buffer to be tested for a mismatch with this buffer
     *
     * @return  The relative index of the first mismatch between this and the
     *          given buffer, otherwise -1 if no mismatch.
     *
     * @since 11
     */
    public int mismatch(FloatBuffer that) {
        int thisPos = this.position();
        int thisRem = this.limit() - thisPos;
        int thatPos = that.position();
        int thatRem = that.limit() - thatPos;
        int length = Math.min(thisRem, thatRem);
        if (length < 0)
            return -1;
        int r = BufferMismatch.mismatch(this, thisPos,
                                        that, thatPos,
                                        length);
        return (r == -1 && thisRem != thatRem) ? length : r;
    }

    // -- Other char stuff --






































































































































































































































    // -- Other byte stuff: Access to binary data --



    /**
     * Retrieves this buffer's byte order.
     *
     * <p> The byte order of a float buffer created by allocation or by
     * wrapping an existing {@code float} array is the {@link
     * ByteOrder#nativeOrder native order} of the underlying
     * hardware.  The byte order of a float buffer created as a <a
     * href="ByteBuffer.html#views">view</a> of a byte buffer is that of the
     * byte buffer at the moment that the view is created.  </p>
     *
     * @return  This buffer's byte order
     */
    public abstract ByteOrder order();

























































































































































































































}
