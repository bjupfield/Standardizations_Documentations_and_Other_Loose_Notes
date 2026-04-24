/*
 * Copyright (c) 2015, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.toolkit;

import javax.lang.model.element.PackageElement;
import javax.tools.FileObject;

/**
 * This is a pseudo element wrapper for the overview element, essentially to
 * associate overview documentation's DocCommentTree to this element.
 */
public class OverviewElement implements DocletElement {

    private final PackageElement pkg;
    private final FileObject fo;

    public OverviewElement(PackageElement pkg, FileObject fo) {
        this.pkg = pkg;
        this.fo = fo;
    }

    @Override
    public PackageElement getPackageElement() {
        return pkg;
    }

    @Override
    public FileObject getFileObject() {
        return fo;
    }

    @Override
    public Kind getSubKind() {
        return Kind.OVERVIEW;
    }
}

