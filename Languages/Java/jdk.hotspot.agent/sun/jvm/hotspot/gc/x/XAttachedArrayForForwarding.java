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
import sun.jvm.hotspot.runtime.VMObjectFactory;
import sun.jvm.hotspot.types.CIntegerField;
import sun.jvm.hotspot.types.Type;
import sun.jvm.hotspot.types.TypeDataBase;

public class XAttachedArrayForForwarding extends VMObject {
    private static CIntegerField lengthField;

    static {
        VM.registerVMInitializedObserver((o, d) -> initialize(VM.getVM().getTypeDataBase()));
    }

    private static synchronized void initialize(TypeDataBase db) {
        Type type = db.lookupType("XAttachedArrayForForwarding");

        lengthField = type.getCIntegerField("_length");
    }

    public XAttachedArrayForForwarding(Address addr) {
        super(addr);
    }

    public long length() {
        return lengthField.getValue(addr);
    }

    // ObjectT: XForwarding
    //  ArrayT: XForwardingEntry
    //
    // template <typename ObjectT, typename ArrayT>
    // inline size_t XAttachedArray<ObjectT, ArrayT>::object_size()
    private long objectSize() {
        return XUtils.alignUp(XForwarding.getSize(), XForwardingEntry.getSize());
    }

    // ArrayT* operator()(const ObjectT* obj) const
    public XForwardingEntry get(XForwarding obj) {
        Address o = obj.getAddress().addOffsetTo(objectSize());
        return VMObjectFactory.newObject(XForwardingEntry.class, o);
    }
}
