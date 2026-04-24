/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
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

package sun.font;

import sun.awt.Win32FontManager;

final class PlatformFontInfo {

    /**
     * The method is only to be called via the
     * {@code FontManagerFactory.getInstance()} factory method.
     */
    static FontManager createFontManager() {
        return new Win32FontManager();
    }
}
