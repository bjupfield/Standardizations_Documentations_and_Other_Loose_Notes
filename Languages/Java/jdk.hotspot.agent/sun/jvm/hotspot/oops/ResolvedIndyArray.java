/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.oops;

import sun.jvm.hotspot.debugger.Address;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.types.Type;
import sun.jvm.hotspot.types.TypeDataBase;
import sun.jvm.hotspot.types.WrongTypeException;
import sun.jvm.hotspot.utilities.GenericArray;
import sun.jvm.hotspot.utilities.Observable;
import sun.jvm.hotspot.utilities.Observer;

public class ResolvedIndyArray extends GenericArray {
    static {
        VM.registerVMInitializedObserver(new Observer() {
            public void update(Observable o, Object data) {
                initialize(VM.getVM().getTypeDataBase());
            }
        });
    }

    private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
        elemType = db.lookupType("ResolvedIndyEntry");

        Type type = db.lookupType("Array<ResolvedIndyEntry>");
        dataFieldOffset = type.getAddressField("_data").getOffset();
    }

    private static long dataFieldOffset;
    protected static Type elemType;

    public ResolvedIndyArray(Address addr) {
        super(addr, dataFieldOffset);
    }

    public ResolvedIndyEntry getAt(int index) {
        if (index < 0 || index >= length()) throw new ArrayIndexOutOfBoundsException(index + " " + length());

        Type elemType = getElemType();

        Address data = getAddress().addOffsetTo(dataFieldOffset);
        long elemSize = elemType.getSize();

        return new ResolvedIndyEntry(data.addOffsetTo(index* elemSize));
    }

    public Type getElemType() {
        return elemType;
    }
}
