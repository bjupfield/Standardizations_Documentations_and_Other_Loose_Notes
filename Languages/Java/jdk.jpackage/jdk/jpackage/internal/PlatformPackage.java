/*
 * Copyright (c) 2019, 2022, Oracle and/or its affiliates. All rights reserved.
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

import java.nio.file.Path;

/**
 *
 * Platform package of an application.
 */
interface PlatformPackage {

    /**
     * Platform-specific package name.
     */
    String name();

    /**
     * Root directory where sources for packaging tool should be stored. On Unix
     * systems contents of this directory will be installed in "/" directory.
     */
    Path sourceRoot();

    /**
     * Source application layout from which to build the package.
     */
    ApplicationLayout sourceApplicationLayout();

    /**
     * Application layout of the installed package.
     */
    ApplicationLayout installedApplicationLayout();
}
