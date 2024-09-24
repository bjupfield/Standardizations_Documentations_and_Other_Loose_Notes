/*
 * Copyright (c) 2017, 2022, Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;

import jdk.javadoc.internal.doclets.toolkit.util.DocPath;

/**
 * The interface for copying doc-files to the output.
 */
public interface DocFilesHandler {
    void copyDocFiles() throws DocletException;
    List<DocPath> getStylesheets() throws DocletException;
}
