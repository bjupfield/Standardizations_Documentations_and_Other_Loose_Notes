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

package sun.jvm.hotspot.gc.z;

import java.io.PrintStream;

import sun.jvm.hotspot.debugger.Address;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.runtime.VMObject;
import sun.jvm.hotspot.runtime.VMObjectFactory;
import sun.jvm.hotspot.types.Type;
import sun.jvm.hotspot.types.TypeDataBase;

// Mirror class for ZHeap

public class ZHeap extends VMObject {

    private static long pageAllocatorFieldOffset;

    static {
        VM.registerVMInitializedObserver((o, d) -> initialize(VM.getVM().getTypeDataBase()));
    }

    private static synchronized void initialize(TypeDataBase db) {
        Type type = db.lookupType("ZHeap");

        pageAllocatorFieldOffset = type.getAddressField("_page_allocator").getOffset();
    }

    public ZHeap(Address addr) {
        super(addr);
    }
    private ZPageAllocator pageAllocator() {
        Address pageAllocatorAddr = addr.addOffsetTo(pageAllocatorFieldOffset);
        return VMObjectFactory.newObject(ZPageAllocator.class, pageAllocatorAddr);
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

    public void printOn(PrintStream tty) {
        tty.print(" ZHeap          ");
        tty.print("used " + (used() / 1024 / 1024) + "M, ");
        tty.print("capacity " + (capacity() / 1024 / 1024) + "M, ");
        tty.println("max capacity " + (maxCapacity() / 1024 / 1024) + "M");
    }
}
