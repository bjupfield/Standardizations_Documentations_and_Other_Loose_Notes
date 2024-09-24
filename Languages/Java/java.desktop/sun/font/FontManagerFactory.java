/*
 * Copyright (c) 2008, 2021, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Factory class used to retrieve a valid FontManager instance for the current
 * platform.
 *
 * A default implementation is given for Linux, Mac OS and Windows.
 */
public final class FontManagerFactory {

    /** Our singleton instance. */
    private static volatile FontManager instance;

    /**
     * Get a valid FontManager implementation for the current platform.
     *
     * @return a valid FontManager instance for the current platform
     */
    public static FontManager getInstance() {

        FontManager result = instance;
        if (result == null) {
            synchronized (FontManagerFactory.class) {
                result = instance;
                if (result == null) {
                    instance = result = PlatformFontInfo.createFontManager();
                }
            }
        }
        return result;
    }
}
