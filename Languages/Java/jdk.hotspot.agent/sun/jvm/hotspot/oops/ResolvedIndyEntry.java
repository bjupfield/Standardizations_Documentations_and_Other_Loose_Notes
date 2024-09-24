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

import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.types.*;
import sun.jvm.hotspot.utilities.*;
import sun.jvm.hotspot.utilities.Observable;
import sun.jvm.hotspot.utilities.Observer;

public class ResolvedIndyEntry extends VMObject {
    private static long          size;
    private static long          baseOffset;
    private static CIntegerField cpIndex;

    static {
        VM.registerVMInitializedObserver(new Observer() {
            public void update(Observable o, Object data) {
                initialize(VM.getVM().getTypeDataBase());
            }
        });
    }

    private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
        Type type = db.lookupType("ResolvedIndyEntry");
        size = type.getSize();

        cpIndex = type.getCIntegerField("_cpool_index");
    }

    ResolvedIndyEntry(Address addr) {
        super(addr);
    }

    public int getConstantPoolIndex() {
        return this.getAddress().getJShortAt(cpIndex.getOffset());
    }

    public void iterateFields(MetadataVisitor visitor) { }
}
