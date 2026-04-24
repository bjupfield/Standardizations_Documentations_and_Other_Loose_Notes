/*
 * Copyright (c) 2005, 2023, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.runtime;

import java.io.*;
import java.util.*;
import sun.jvm.hotspot.memory.*;
import sun.jvm.hotspot.oops.*;

public class ConcurrentLocksPrinter {
    private final Map<JavaThread, List<Oop>> locksMap = new HashMap<>();

    public ConcurrentLocksPrinter() {
        fillLocks();
    }

    public void print(JavaThread jthread, PrintStream tty) {
        List<Oop> locks = locksMap.get(jthread);
        tty.println("Locked ownable synchronizers:");
        if (locks == null || locks.isEmpty()) {
            tty.println("    - None");
        } else {
            for (Oop oop : locks) {
                tty.println("    - <" + oop.getHandle() + ">, (a " +
                       oop.getKlass().getName().asString() + ")");
            }
        }
    }

    //-- Internals only below this point
    private JavaThread getOwnerThread(Oop oop) {
        Oop threadOop = OopUtilities.abstractOwnableSynchronizerGetOwnerThread(oop);
        if (threadOop == null) {
            return null;
        } else {
            return OopUtilities.threadOopGetJavaThread(threadOop);
        }
    }

    private void fillLocks() {
        VM vm = VM.getVM();
        SystemDictionary sysDict = vm.getSystemDictionary();
        Klass absOwnSyncKlass = sysDict.getAbstractOwnableSynchronizerKlass();
        ObjectHeap heap = vm.getObjectHeap();
        // may be not loaded at all
        if (absOwnSyncKlass != null) {
            heap.iterateObjectsOfKlass(new DefaultHeapVisitor() {
                    public boolean doObj(Oop oop) {
                        JavaThread thread = getOwnerThread(oop);
                        if (thread != null) {
                            locksMap.computeIfAbsent(thread, t -> new ArrayList<>())
                                    .add(oop);
                        }
                        return false;
                    }

                }, absOwnSyncKlass, true);
        }
    }
}
