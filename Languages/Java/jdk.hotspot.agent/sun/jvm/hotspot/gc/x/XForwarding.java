/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021, NTT DATA.
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

import java.util.Iterator;

import sun.jvm.hotspot.debugger.Address;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.runtime.VMObject;
import sun.jvm.hotspot.runtime.VMObjectFactory;
import sun.jvm.hotspot.types.CIntegerField;
import sun.jvm.hotspot.types.Type;
import sun.jvm.hotspot.types.TypeDataBase;

public class XForwarding extends VMObject {
    private static Type type;
    private static long virtualFieldOffset;
    private static long entriesFieldOffset;
    private static CIntegerField objectAlignmentShiftField;
    private static CIntegerField refCountField;

    static {
        VM.registerVMInitializedObserver((o, d) -> initialize(VM.getVM().getTypeDataBase()));
    }

    private static synchronized void initialize(TypeDataBase db) {
        type = db.lookupType("XForwarding");

        virtualFieldOffset = type.getField("_virtual").getOffset();
        entriesFieldOffset = type.getField("_entries").getOffset();
        objectAlignmentShiftField = type.getCIntegerField("_object_alignment_shift");
        refCountField = type.getCIntegerField("_ref_count");
    }

    public XForwarding(Address addr) {
        super(addr);
    }

    public static long getSize() {
        return type.getSize();
    }

    private XVirtualMemory virtual() {
        return VMObjectFactory.newObject(XVirtualMemory.class, addr.addOffsetTo(virtualFieldOffset));
    }

    private XAttachedArrayForForwarding entries() {
        return VMObjectFactory.newObject(XAttachedArrayForForwarding.class, addr.addOffsetTo(entriesFieldOffset));
    }

    public long start() {
        return virtual().start();
    }

    public int objectAlignmentShift() {
        return (int)objectAlignmentShiftField.getValue(addr);
    }

    public boolean retainPage() {
        return refCountField.getValue(addr) > 0;
    }

    private XForwardingEntry at(long cursor) {
        long offset = XForwardingEntry.getSize() * cursor;
        Address entryAddress = entries().get(this).getAddress().addOffsetTo(offset);
        return VMObjectFactory.newObject(XForwardingEntry.class, entryAddress);
    }

    private class XForwardEntryIterator implements Iterator<XForwardingEntry> {

        private long cursor;

        private XForwardingEntry nextEntry;

        public XForwardEntryIterator(long fromIndex) {
            long mask = entries().length() - 1;
            long hash = XHash.uint32_to_uint32(fromIndex);
            cursor = hash & mask;
            nextEntry = at(cursor);
        }

        @Override
        public boolean hasNext() {
            return nextEntry.populated();
        }

        @Override
        public XForwardingEntry next() {
            XForwardingEntry entry = nextEntry;

            long mask = entries().length() - 1;
            cursor = (cursor + 1) & mask;
            nextEntry = at(cursor);

            return entry;
        }

        public XForwardingEntry peak() {
            return nextEntry;
        }
    }

    public XForwardingEntry find(long fromIndex) {
        XForwardEntryIterator itr = new XForwardEntryIterator(fromIndex);
        while (itr.hasNext()) {
            XForwardingEntry entry = itr.next();
            if (entry.fromIndex() == fromIndex) {
                return entry;
            }
        }
        return itr.peak();
    }
}
