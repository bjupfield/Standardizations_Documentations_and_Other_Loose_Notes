/*
 * Copyright (c) 2018, 2022, Oracle and/or its affiliates. All rights reserved.
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
import sun.jvm.hotspot.runtime.VMObjectFactory;

class XPageTableEntry {
    Address entry;

    XPageTableEntry(Address address) {
        entry = address;
    }

    XPage page() {
        return VMObjectFactory.newObject(XPage.class, zPageBits());
    }

    private Address zPageBits() {
        return entry.andWithMask(~1L);
    }

    boolean relocating() {
        return (entry.asLongValue() & 1) == 1;
    }

    boolean isEmpty() {
        return entry == null || zPageBits() == null;
    }
}
