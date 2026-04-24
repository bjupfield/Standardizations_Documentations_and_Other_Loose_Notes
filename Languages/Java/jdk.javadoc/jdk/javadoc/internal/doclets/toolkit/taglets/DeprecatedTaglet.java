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

import java.util.EnumSet;
import javax.lang.model.element.Element;

import com.sun.source.doctree.DocTree;
import jdk.javadoc.doclet.Taglet.Location;
import jdk.javadoc.internal.doclets.toolkit.Content;

/**
 * A taglet that represents the {@code @deprecated} tag.
 */
public class DeprecatedTaglet extends BaseTaglet {

    public DeprecatedTaglet() {
        super(DocTree.Kind.DEPRECATED, false,
                EnumSet.of(Location.MODULE, Location.TYPE, Location.CONSTRUCTOR, Location.METHOD, Location.FIELD));
    }

    @Override
    public Content getAllBlockTagOutput(Element holder, TagletWriter writer) {
        return writer.deprecatedTagOutput(holder);
    }
}
