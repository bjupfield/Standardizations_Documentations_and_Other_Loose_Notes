/*
 * Copyright (c) 2000, 2023, Oracle and/or its affiliates. All rights reserved.
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

import java.util.*;

import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.types.*;
import sun.jvm.hotspot.runtime.win32_x86.Win32X86JavaThreadPDAccess;
import sun.jvm.hotspot.runtime.win32_amd64.Win32AMD64JavaThreadPDAccess;
import sun.jvm.hotspot.runtime.win32_aarch64.Win32AARCH64JavaThreadPDAccess;
import sun.jvm.hotspot.runtime.linux_x86.LinuxX86JavaThreadPDAccess;
import sun.jvm.hotspot.runtime.linux_amd64.LinuxAMD64JavaThreadPDAccess;
import sun.jvm.hotspot.runtime.linux_aarch64.LinuxAARCH64JavaThreadPDAccess;
import sun.jvm.hotspot.runtime.linux_riscv64.LinuxRISCV64JavaThreadPDAccess;
import sun.jvm.hotspot.runtime.linux_ppc64.LinuxPPC64JavaThreadPDAccess;
import sun.jvm.hotspot.runtime.bsd_x86.BsdX86JavaThreadPDAccess;
import sun.jvm.hotspot.runtime.bsd_amd64.BsdAMD64JavaThreadPDAccess;
import sun.jvm.hotspot.runtime.bsd_aarch64.BsdAARCH64JavaThreadPDAccess;
import sun.jvm.hotspot.utilities.*;
import sun.jvm.hotspot.utilities.Observable;
import sun.jvm.hotspot.utilities.Observer;

class ThreadsList extends VMObject {
    private static AddressField  threadsField;
    private static CIntegerField lengthField;

    static {
        VM.registerVMInitializedObserver((o, d) -> initialize(VM.getVM().getTypeDataBase()));
    }

    private static synchronized void initialize(TypeDataBase db) {
        Type type = db.lookupType("ThreadsList");
        lengthField = type.getCIntegerField("_length");
        threadsField = type.getAddressField("_threads");
    }

    public Address getJavaThreadAddressAt(int i) {
      Address threadAddr = threadsField.getValue(addr);
      Address at = threadAddr.getAddressAt(VM.getVM().getAddressSize() * i);
      return at;
    }

    public long length() {
        return lengthField.getValue(addr);
    }

    public ThreadsList(Address addr) {
        super(addr);
    }
}

public class Threads {
    private static JavaThreadFactory threadFactory;
    private static AddressField      threadListField;
    private static VirtualConstructor virtualConstructor;
    private static JavaThreadPDAccess access;
    private static ThreadsList _list;

    static {
        VM.registerVMInitializedObserver(new Observer() {
            public void update(Observable o, Object data) {
                initialize(VM.getVM().getTypeDataBase());
            }
        });
    }

    private static synchronized void initialize(TypeDataBase db) {
        Type type = db.lookupType("ThreadsSMRSupport");
        threadListField = type.getAddressField("_java_thread_list");

        // Instantiate appropriate platform-specific JavaThreadFactory
        String os  = VM.getVM().getOS();
        String cpu = VM.getVM().getCPU();

        access = null;
        // FIXME: find the platform specific PD class by reflection?
        if (os.equals("win32")) {
            if (cpu.equals("x86")) {
                access =  new Win32X86JavaThreadPDAccess();
            } else if (cpu.equals("amd64")) {
                access =  new Win32AMD64JavaThreadPDAccess();
            } else if (cpu.equals("aarch64")) {
                access =  new Win32AARCH64JavaThreadPDAccess();
            }
        } else if (os.equals("linux")) {
            if (cpu.equals("x86")) {
                access = new LinuxX86JavaThreadPDAccess();
            } else if (cpu.equals("amd64")) {
                access = new LinuxAMD64JavaThreadPDAccess();
            } else if (cpu.equals("ppc64")) {
                access = new LinuxPPC64JavaThreadPDAccess();
            } else if (cpu.equals("aarch64")) {
                access = new LinuxAARCH64JavaThreadPDAccess();
            } else if (cpu.equals("riscv64")) {
                access = new LinuxRISCV64JavaThreadPDAccess();
            } else {
              try {
                access = (JavaThreadPDAccess)
                  Class.forName("sun.jvm.hotspot.runtime.linux_" +
                     cpu.toLowerCase() + ".Linux" + cpu.toUpperCase() +
                     "JavaThreadPDAccess").getDeclaredConstructor().newInstance();
              } catch (Exception e) {
                throw new RuntimeException("OS/CPU combination " + os + "/" + cpu +
                                           " not yet supported");
              }
            }
        } else if (os.equals("bsd")) {
            if (cpu.equals("x86")) {
                access = new BsdX86JavaThreadPDAccess();
            } else if (cpu.equals("amd64") || cpu.equals("x86_64")) {
                access = new BsdAMD64JavaThreadPDAccess();
            }
        } else if (os.equals("darwin")) {
            if (cpu.equals("amd64") || cpu.equals("x86_64")) {
                access = new BsdAMD64JavaThreadPDAccess();
            } else if (cpu.equals("aarch64")) {
                access = new BsdAARCH64JavaThreadPDAccess();
            }
        }

        if (access == null) {
            throw new RuntimeException("OS/CPU combination " + os + "/" + cpu +
            " not yet supported");
        }

        virtualConstructor = new VirtualConstructor(db);
        // Add mappings for all known thread types
        virtualConstructor.addMapping("JavaThread", JavaThread.class);
        if (!VM.getVM().isCore()) {
            virtualConstructor.addMapping("CompilerThread", CompilerThread.class);
        }
        virtualConstructor.addMapping("JvmtiAgentThread", JvmtiAgentThread.class);
        virtualConstructor.addMapping("ServiceThread", ServiceThread.class);
        virtualConstructor.addMapping("MonitorDeflationThread", MonitorDeflationThread.class);
        virtualConstructor.addMapping("NotificationThread", NotificationThread.class);
        virtualConstructor.addMapping("StringDedupThread", StringDedupThread.class);
    }

    public Threads() {
        _list = VMObjectFactory.newObject(ThreadsList.class, threadListField.getValue());
    }

    /** NOTE: this returns objects of type JavaThread, CompilerThread,
      JvmtiAgentThread, NotificationThread, MonitorDeflationThread and ServiceThread.
      The latter four are subclasses of the former. Most operations
      (fetching the top frame, etc.) are only allowed to be performed on
      a "pure" JavaThread. For this reason, {@link
      sun.jvm.hotspot.runtime.JavaThread#isJavaThread} has been
      changed from the definition in the VM (which returns true for
      all of these thread types) to return true for JavaThreads and
      false for the four subclasses. FIXME: should reconsider the
      inheritance hierarchy; see {@link
      sun.jvm.hotspot.runtime.JavaThread#isJavaThread}. */
    public JavaThread getJavaThreadAt(int i) {
        if (i < _list.length()) {
            return createJavaThreadWrapper(_list.getJavaThreadAddressAt(i));
        }
        return null;
    }

    public int getNumberOfThreads() {
        return (int) _list.length();
    }

    /** Routine for instantiating appropriately-typed wrapper for a
      JavaThread. Currently needs to be public for OopUtilities to
      access it. */
    public JavaThread createJavaThreadWrapper(Address threadAddr) {
        try {
            JavaThread thread = (JavaThread)virtualConstructor.instantiateWrapperFor(threadAddr);
            thread.setThreadPDAccess(access);
            return thread;
        } catch (Exception e) {
            throw new RuntimeException("Unable to deduce type of thread from address " + threadAddr +
            " (expected type JavaThread, CompilerThread, MonitorDeflationThread, ServiceThread or JvmtiAgentThread)", e);
        }
    }

    /** Memory operations */
    public void oopsDo(AddressVisitor oopVisitor) {
        // FIXME: add more of VM functionality
        Threads threads = VM.getVM().getThreads();
        for (int i = 0; i < threads.getNumberOfThreads(); i++) {
            JavaThread thread = threads.getJavaThreadAt(i);
            thread.oopsDo(oopVisitor);
        }
    }

    // refer to Threads::owning_thread_from_monitor_owner
    public JavaThread owningThreadFromMonitor(Address o) {
        assert(VM.getVM().getCommandLineFlag("LockingMode").getInt() != LockingMode.getLightweight());
        if (o == null) return null;
        for (int i = 0; i < getNumberOfThreads(); i++) {
            JavaThread thread = getJavaThreadAt(i);
            if (o.equals(thread.threadObjectAddress())) {
                return thread;
            }
        }

        for (int i = 0; i < getNumberOfThreads(); i++) {
            JavaThread thread = getJavaThreadAt(i);
            if (thread.isLockOwned(o))
                return thread;
        }
        return null;
    }

    public JavaThread owningThreadFromMonitor(ObjectMonitor monitor) {
        if (VM.getVM().getCommandLineFlag("LockingMode").getInt() == LockingMode.getLightweight()) {
            if (monitor.isOwnedAnonymous()) {
                OopHandle object = monitor.object();
                for (int i = 0; i < getNumberOfThreads(); i++) {
                    JavaThread thread = getJavaThreadAt(i);
                    if (thread.isLockOwned(object)) {
                        return thread;
                     }
                }
                // We should have found the owner, however, as the VM could be in any state, including the middle
                // of performing GC, it is not always possible to do so. Just return null if we can't locate it.
                System.out.println("Warning: We failed to find a thread that owns an anonymous lock. This is likely");
                System.out.println("due to the JVM currently running a GC. Locking information may not be accurate.");
                return null;
            }
            // Owner can only be threads at this point.
            Address o = monitor.owner();
            if (o == null) return null;
            return new JavaThread(o);
        } else {
            return owningThreadFromMonitor(monitor.owner());
        }
    }

    // refer to Threads::get_pending_threads
    // Get list of Java threads that are waiting to enter the specified monitor.
    public List<JavaThread> getPendingThreads(ObjectMonitor monitor) {
        List<JavaThread> pendingThreads = new ArrayList<>();
        for (int i = 0; i < getNumberOfThreads(); i++) {
            JavaThread thread = getJavaThreadAt(i);
            if (thread.isCompilerThread() || thread.isCodeCacheSweeperThread()) {
                continue;
            }
            ObjectMonitor pending = thread.getCurrentPendingMonitor();
            if (monitor.equals(pending)) {
                pendingThreads.add(thread);
            }
        }
        return pendingThreads;
    }

    // Get list of Java threads that have called Object.wait on the specified monitor.
    public List<JavaThread> getWaitingThreads(ObjectMonitor monitor) {
        List<JavaThread> pendingThreads = new ArrayList<>();
        for (int i = 0; i < getNumberOfThreads(); i++) {
            JavaThread thread = getJavaThreadAt(i);
            ObjectMonitor waiting = thread.getCurrentWaitingMonitor();
            if (monitor.equals(waiting)) {
                pendingThreads.add(thread);
            }
        }
        return pendingThreads;
    }

    // FIXME: add other accessors
}
