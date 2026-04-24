/*
 * Copyright (c) 2000, 2020, Oracle and/or its affiliates. All rights reserved.
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
 */

package sun.jvm.hotspot.code;

import java.io.PrintStream;

import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.utilities.*;

public class CompressedReadStream extends CompressedStream {
  /** Equivalent to CompressedReadStream(buffer, 0) */
  public CompressedReadStream(Address buffer) {
    this(buffer, 0);
  }

  public CompressedReadStream(Address buffer, int position) {
    super(buffer, position);
  }

  public boolean readBoolean() {
    return (read() != 0);
  }

  public byte readByte() {
    return (byte) read();
  }

  public char readChar() {
    return (char) readInt();
  }

  public short readShort() {
    return (short) readSignedInt();
  }

  public int readSignedInt() {
    return decodeSign(readInt());
  }

  public float readFloat() {
    return Float.intBitsToFloat(reverseInt(readInt()));
  }

  public double readDouble() {
    int rh = readInt();
    int rl = readInt();
    int h = reverseInt(rh);
    int l = reverseInt(rl);
    return Double.longBitsToDouble(((long)h << 32) | ((long)l & 0x00000000FFFFFFFFL));
  }

  public long readLong() {
    long low = readSignedInt() & 0x00000000FFFFFFFFL;
    long high = readSignedInt();
    return (high << 32) | low;
  }

  //--------------------------------------------------------------------------------
  public int readInt() {
    // UNSIGNED5::read_uint(_buffer, &_position, limit=0)
    return (int) Unsigned5.readUint(this, position,
                                    // bytes are fetched here:
                                    CompressedReadStream::read,
                                    // updated position comes through here:
                                    CompressedReadStream::setPosition);
  }

  private short read(int index) {
    return (short) buffer.getCIntegerAt(index, 1, true);
  }

  /** Reads an unsigned byte, but returns it as a short */
  private short read() {
    short retval = (short) buffer.getCIntegerAt(position, 1, true);
    ++position;
    return retval;
  }


  /**
   * Dumps the stream, making an assumption that all items are encoded
   * as UNSIGNED5.  The sizeLimit argument tells the dumper when to
   * stop trying to read bytes; if it is zero, the dumper goes as long
   * as it can until it encounters a null byte.
   *
   * This class mixes UNSIGNED5 with other formats.  Stray bytes are
   * decoded either as "null" (0x00), one less than the byte value
   * (0x01..0xBF) or as part of a spurious multi-byte encoding.
   * Proceed with caution.
   */
  public void dump() { dumpOn(System.out, 0); }
  public void dump(int sizeLimit) { dumpOn(System.out, sizeLimit); }
  public void dumpOn(PrintStream tty, int sizeLimit) {
      new Unsigned5(buffer, sizeLimit).dumpOn(tty, -1);
  }
}
