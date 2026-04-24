/*
 * Copyright (c) 2007, 2023, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.nio.file;

import java.nio.file.WatchEvent.Modifier;
import jdk.internal.misc.FileSystemOption;

/**
 * Defines the <em>sensitivity levels</em> when registering objects with a
 * watch service implementation that polls the file system.
 *
 * @deprecated
 * The sensitivity levels were historically used by polling-based
 * {@link java.nio.file.WatchService WatchService} implementations to configure
 * the polling interval. They are are no longer used. The {@code WatchService}
 * implementations in the JDK ignore these {@link java.nio.file.WatchEvent
 * WatchEvent} modifiers if they are specified when registering a directory
 * to be watched.
 *
 * @since 1.7
 */

@Deprecated(since="21", forRemoval = true)
public enum SensitivityWatchEventModifier implements Modifier {
    /**
     * High sensitivity.
     */
    HIGH(FileSystemOption.SENSITIVITY_HIGH, 2),
    /**
     * Medium sensitivity.
     */
    MEDIUM(FileSystemOption.SENSITIVITY_MEDIUM, 10),
    /**
     * Low sensitivity.
     */
    LOW(FileSystemOption.SENSITIVITY_LOW, 30);

    /**
     * Returns the sensitivity in seconds.
     */
    public int sensitivityValueInSeconds() {
        return sensitivity;
    }

    private final int sensitivity;
    private SensitivityWatchEventModifier(FileSystemOption<Integer> option,
                                          int sensitivity) {
        this.sensitivity = sensitivity;
        option.register(this, sensitivity);
    }
}
