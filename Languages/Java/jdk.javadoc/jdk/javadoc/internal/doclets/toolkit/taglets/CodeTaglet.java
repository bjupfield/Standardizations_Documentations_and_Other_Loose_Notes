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
import com.sun.source.doctree.LiteralTree;
import jdk.javadoc.doclet.Taglet.Location;
import jdk.javadoc.internal.doclets.toolkit.Content;

/**
 * An inline taglet used to denote literal code fragments.
 * The enclosed text is interpreted as not containing HTML markup or
 * nested javadoc tags, and is rendered in a font suitable for code.
 *
 * <p> The tag {@code {@code ...}} is equivalent to
 * {@code <code>{@literal ...}</code>}.
 * For example, the text:
 * <blockquote>  The type {@code {@code List<P>}}  </blockquote>
 * displays as:
 * <blockquote>  The type {@code List<P>}  </blockquote>
 */
public class CodeTaglet extends BaseTaglet {

    CodeTaglet() {
        super(DocTree.Kind.CODE, true, EnumSet.allOf(Location.class));
    }

    @Override
    public Content getInlineTagOutput(Element element, DocTree tag, TagletWriter writer) {
        return writer.codeTagOutput(element, (LiteralTree) tag);
    }
}
