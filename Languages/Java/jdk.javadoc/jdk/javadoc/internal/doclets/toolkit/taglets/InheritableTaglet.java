/*
 * Copyright (c) 2003, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.toolkit.taglets;


import java.util.List;

import javax.lang.model.element.Element;

import com.sun.source.doctree.DocTree;
import jdk.javadoc.internal.doclets.toolkit.BaseConfiguration;

/**
 * A taglet should implement this interface if it supports an {@code {@inheritDoc}}
 * tag or is automatically inherited if it is missing.
 */
public interface InheritableTaglet extends Taglet {

    /*
     * Called by InheritDocTaglet on an inheritable taglet to expand {@inheritDoc}
     * found inside a tag corresponding to that taglet.
     *
     * When inheriting failed some assumption, or caused an error, the taglet
     * can return either of:
     *
     *   - new Output(null, null, List.of(), false)
     *   - new Output(null, null, List.of(), true)
     *
     * In the future, this could be reworked using some other mechanism,
     * such as throwing an exception.
     */
    Output inherit(Element owner, DocTree tag, boolean isFirstSentence, BaseConfiguration configuration);

    record Output(DocTree holderTag,
                  Element holder,
                  List<? extends DocTree> inlineTags,
                  boolean isValidInheritDocTag) {
    }
}
