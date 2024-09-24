/*
 * Copyright Amazon.com Inc. or its affiliates. All Rights Reserved.
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

import sun.jvm.hotspot.types.TypeDataBase;


/** Encapsulates the LockingMode enum in globalDefinitions.hpp in
    the VM. */

public class LockingMode {
  private static int monitor;
  private static int legacy;
  private static int lightweight;

  static {
    VM.registerVMInitializedObserver(
        (o, d) -> initialize(VM.getVM().getTypeDataBase()));
  }

  private static synchronized void initialize(TypeDataBase db) {
    monitor     = db.lookupIntConstant("LM_MONITOR").intValue();
    legacy      = db.lookupIntConstant("LM_LEGACY").intValue();
    lightweight = db.lookupIntConstant("LM_LIGHTWEIGHT").intValue();
  }

  public static int getMonitor() {
    return monitor;
  }

  public static int getLegacy() {
    return legacy;
  }

  public static int getLightweight() {
    return lightweight;
  }
}
