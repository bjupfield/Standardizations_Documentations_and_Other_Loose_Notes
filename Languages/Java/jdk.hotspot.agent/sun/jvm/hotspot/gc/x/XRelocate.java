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

import sun.jvm.hotspot.debugger.Address;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.runtime.VMObject;
import sun.jvm.hotspot.types.AddressField;
import sun.jvm.hotspot.types.Type;
import sun.jvm.hotspot.types.TypeDataBase;

public class XRelocate  extends VMObject {

    static {
        VM.registerVMInitializedObserver((o, d) -> initialize(VM.getVM().getTypeDataBase()));
    }

    private static synchronized void initialize(TypeDataBase db) {
        Type type = db.lookupType("XRelocate");
    }

    public XRelocate(Address addr) {
        super(addr);
    }

    private long forwardingIndex(XForwarding forwarding, Address from) {
        long fromOffset = XAddress.offset(from);
        return (fromOffset - forwarding.start()) >>> forwarding.objectAlignmentShift();
    }

    private Address forwardingFind(XForwarding forwarding, Address from) {
        long fromIndex = forwardingIndex(forwarding, from);
        XForwardingEntry entry = forwarding.find(fromIndex);
        return entry.populated() ? XAddress.good(VM.getVM().getDebugger().newAddress(entry.toOffset())) : null;
    }

    public Address forwardObject(XForwarding forwarding, Address from) {
        return forwardingFind(forwarding, from);
    }

    public Address relocateObject(XForwarding forwarding, Address o) {
        Address toAddr = forwardingFind(forwarding, o);
        if (toAddr != null) {
            // Already relocated.
            return toAddr;
        } else {
            // Return original address because it is not yet relocated.
            return o;
        }
    }
}
