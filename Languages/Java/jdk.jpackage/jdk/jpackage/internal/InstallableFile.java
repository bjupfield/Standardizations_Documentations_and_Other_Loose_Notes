/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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
import java.util.Objects;

final class InstallableFile {

    InstallableFile(Path srcPath, Path installPath) {
        Objects.requireNonNull(srcPath);

        this.srcPath = srcPath;
        this.installPath = installPath;
    }

    Path installPath() {
        return installPath;
    }

    Path srcPath() {
        return srcPath;
    }

    void applyToApplicationLayouts(ApplicationLayout src,
            ApplicationLayout install) {
        var key = new Object();
        src.pathGroup().setPath(key, srcPath);
        if (installPath != null && install != null) {
            install.pathGroup().setPath(key, installPath);
        }
    }

    void excludeFromApplicationLayout(ApplicationLayout layout) {
        applyToApplicationLayouts(layout, null);
    }

    private final Path installPath;
    private final Path srcPath;
}
