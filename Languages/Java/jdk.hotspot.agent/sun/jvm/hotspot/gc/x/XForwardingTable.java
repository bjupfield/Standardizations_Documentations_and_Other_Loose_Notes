/*
 * Copyright (c) 2018, 2021, Oracle and/or its affiliates. All rights reserved.
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
import sun.jvm.hotspot.runtime.VMObjectFactory;
import sun.jvm.hotspot.types.AddressField;
import sun.jvm.hotspot.types.CIntegerField;
import sun.jvm.hotspot.types.Type;
import sun.jvm.hotspot.types.TypeDataBase;

public class XForwardingTable extends VMObject {
    private static long mapFieldOffset;

    static {
        VM.registerVMInitializedObserver((o, d) -> initialize(VM.getVM().getTypeDataBase()));
    }

    private static synchronized void initialize(TypeDataBase db) {
        Type type = db.lookupType("XForwardingTable");

        mapFieldOffset = type.getAddressField("_map").getOffset();
    }

    public XForwardingTable(Address addr) {
        super(addr);
    }

    private XGranuleMapForForwarding map() {
        return VMObjectFactory.newObject(XGranuleMapForForwarding.class, addr.addOffsetTo(mapFieldOffset));
    }

    public XForwarding get(Address o) {
        return VMObjectFactory.newObject(XForwarding.class, map().get(XAddress.offset(o)));
    }
}
