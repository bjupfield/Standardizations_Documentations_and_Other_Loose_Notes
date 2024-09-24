/*
 * Copyright (c) 2019, 2021, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.gc.x;

import java.util.HashMap;

import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.utilities.BitMap;
import sun.jvm.hotspot.utilities.BitMapInterface;

/** Discontiguous bitmap for ZGC. */
public class XExternalBitMap implements BitMapInterface {
    private XPageTable pageTable;
    private final long oopSize;

    private HashMap<XPage, BitMap> pageToBitMap = new HashMap<XPage, BitMap>();

    public XExternalBitMap(XCollectedHeap collectedHeap) {
        pageTable = collectedHeap.heap().pageTable();
        oopSize = VM.getVM().getOopSize();
    }

    private XPage getPage(long zOffset) {
        if (zOffset > XGlobals.XAddressOffsetMask()) {
            throw new RuntimeException("Not a Z offset: " + zOffset);
        }

        XPage page = pageTable.get(XUtils.longToAddress(zOffset));
        if (page == null) {
            throw new RuntimeException("Address not in pageTable: " + zOffset);
        }
        return page;
    }

    private BitMap getOrAddBitMap(XPage page) {
        BitMap bitMap = pageToBitMap.get(page);
        if (bitMap == null) {
            long size = page.size();

            long maxNumObjects = size >>> page.object_alignment_shift();
            if (maxNumObjects > Integer.MAX_VALUE) {
                throw new RuntimeException("int overflow");
            }
            int intMaxNumObjects = (int)maxNumObjects;

            bitMap = new BitMap(intMaxNumObjects);
            pageToBitMap.put(page,  bitMap);
        }

        return bitMap;
    }

    private int pageLocalBitMapIndex(XPage page, long zOffset) {
        long pageLocalZOffset = zOffset - page.start();
        return (int)(pageLocalZOffset >>> page.object_alignment_shift());
    }

    private long convertToZOffset(long offset) {
        long addr = oopSize * offset;
        return addr & XGlobals.XAddressOffsetMask();
    }

    @Override
    public boolean at(long offset) {
        long zOffset = convertToZOffset(offset);
        XPage page = getPage(zOffset);
        BitMap bitMap = getOrAddBitMap(page);
        int index = pageLocalBitMapIndex(page, zOffset);

        return bitMap.at(index);
    }

    @Override
    public void atPut(long offset, boolean value) {
        long zOffset = convertToZOffset(offset);
        XPage page = getPage(zOffset);
        BitMap bitMap = getOrAddBitMap(page);
        int index = pageLocalBitMapIndex(page, zOffset);

        bitMap.atPut(index, value);
    }

    @Override
    public void clear() {
        for (BitMap bitMap : pageToBitMap.values()) {
            bitMap.clear();
        }
    }
}
