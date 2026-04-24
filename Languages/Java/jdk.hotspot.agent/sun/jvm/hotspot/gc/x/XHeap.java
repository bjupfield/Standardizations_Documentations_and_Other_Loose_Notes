/*
 * Copyright (c) 2017, 2022, Oracle and/or its affiliates. All rights reserved.
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

import java.io.PrintStream;

import sun.jvm.hotspot.debugger.Address;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.runtime.VMObject;
import sun.jvm.hotspot.runtime.VMObjectFactory;
import sun.jvm.hotspot.types.Type;
import sun.jvm.hotspot.types.TypeDataBase;

// Mirror class for XHeap

public class XHeap extends VMObject {

    private static long pageAllocatorFieldOffset;
    private static long pageTableFieldOffset;
    private static long forwardingTableFieldOffset;
    private static long relocateFieldOffset;

    static {
        VM.registerVMInitializedObserver((o, d) -> initialize(VM.getVM().getTypeDataBase()));
    }

    private static synchronized void initialize(TypeDataBase db) {
        Type type = db.lookupType("XHeap");

        pageAllocatorFieldOffset = type.getAddressField("_page_allocator").getOffset();
        pageTableFieldOffset = type.getAddressField("_page_table").getOffset();
        forwardingTableFieldOffset = type.getAddressField("_forwarding_table").getOffset();
        relocateFieldOffset = type.getAddressField("_relocate").getOffset();
    }

    public XHeap(Address addr) {
        super(addr);
    }

    private XPageAllocator pageAllocator() {
        Address pageAllocatorAddr = addr.addOffsetTo(pageAllocatorFieldOffset);
        return VMObjectFactory.newObject(XPageAllocator.class, pageAllocatorAddr);
    }

    XPageTable pageTable() {
        return VMObjectFactory.newObject(XPageTable.class, addr.addOffsetTo(pageTableFieldOffset));
    }

    XForwardingTable forwardingTable() {
        return VMObjectFactory.newObject(XForwardingTable.class, addr.addOffsetTo(forwardingTableFieldOffset));
    }

    XRelocate relocate() {
        return VMObjectFactory.newObject(XRelocate.class, addr.addOffsetTo(relocateFieldOffset));
    }

    public long maxCapacity() {
        return pageAllocator().maxCapacity();
    }

    public long capacity() {
        return pageAllocator().capacity();
    }

    public long used() {
        return pageAllocator().used();
    }

    boolean is_relocating(Address o) {
        return pageTable().is_relocating(o);
    }

    Address relocate_object(Address addr) {
        XForwarding forwarding = forwardingTable().get(addr);
        if (forwarding == null) {
            return XAddress.good(addr);
        }
        return relocate().relocateObject(forwarding, XAddress.good(addr));
    }

    public boolean isIn(Address addr) {
        if (XAddress.isIn(addr)) {
            XPage page = pageTable().get(addr);
            if (page != null) {
                return page.isIn(addr);
            }
        }
        return false;
    }

    public Address remapObject(Address o) {
        XForwarding forwarding = forwardingTable().get(addr);
        if (forwarding == null) {
            return XAddress.good(o);
        }
        return relocate().forwardObject(forwarding, XAddress.good(o));
    }

    public void printOn(PrintStream tty) {
        tty.print(" ZHeap          ");
        tty.print("used " + (used() / 1024 / 1024) + "M, ");
        tty.print("capacity " + (capacity() / 1024 / 1024) + "M, ");
        tty.println("max capacity " + (maxCapacity() / 1024 / 1024) + "M");
    }
}
