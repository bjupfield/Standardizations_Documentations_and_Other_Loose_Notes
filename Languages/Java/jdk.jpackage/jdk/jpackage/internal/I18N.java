/*
 * Copyright (c) 2019, 2023, Oracle and/or its affiliates. All rights reserved.
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
 *
 */
package jdk.jpackage.internal;

import jdk.internal.util.OperatingSystem;

import java.util.ResourceBundle;

class I18N {

    static String getString(String key) {
        if (PLATFORM.containsKey(key)) {
            return PLATFORM.getString(key);
        }
        return SHARED.getString(key);
    }

    private static final ResourceBundle SHARED = ResourceBundle.getBundle(
            "jdk.jpackage.internal.resources.MainResources");

    private static final ResourceBundle PLATFORM;

    static {
        if (OperatingSystem.isLinux()) {
            PLATFORM = ResourceBundle.getBundle(
                    "jdk.jpackage.internal.resources.LinuxResources");
        } else if (OperatingSystem.isWindows()) {
            PLATFORM = ResourceBundle.getBundle(
                    "jdk.jpackage.internal.resources.WinResources");
        } else if (OperatingSystem.isMacOS()) {
            PLATFORM = ResourceBundle.getBundle(
                    "jdk.jpackage.internal.resources.MacResources");
        } else {
            throw new IllegalStateException("Unknown platform");
        }
    }
}
