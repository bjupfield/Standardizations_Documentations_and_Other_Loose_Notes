/*
 * Copyright (c) 2001, 2023, Oracle and/or its affiliates. All rights reserved.
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
import java.util.List;
import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.SeeTree;
import jdk.javadoc.doclet.Taglet.Location;
import jdk.javadoc.internal.doclets.toolkit.BaseConfiguration;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.CommentHelper;
import jdk.javadoc.internal.doclets.toolkit.util.DocFinder.Result;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

/**
 * A taglet that represents the {@code @see} tag.
 */
public class SeeTaglet extends BaseTaglet implements InheritableTaglet {

    public SeeTaglet() {
        super(DocTree.Kind.SEE, false, EnumSet.allOf(Location.class));
    }

    @Override
    public Output inherit(Element owner, DocTree tag, boolean isFirstSentence, BaseConfiguration configuration) {
        CommentHelper ch = configuration.utils.getCommentHelper(owner);
        var path = ch.getDocTreePath(tag);
        configuration.getMessages().warning(path, "doclet.inheritDocWithinInappropriateTag");
        return new Output(null, null, List.of(), true /* true, otherwise there will be an exception up the stack */);
    }

    @Override
    public Content getAllBlockTagOutput(Element holder, TagletWriter writer) {
        Utils utils = writer.configuration().utils;
        List<? extends SeeTree> tags = utils.getSeeTrees(holder);
        Element e = holder;
        if (utils.isMethod(holder)) {
            var docFinder = utils.docFinder();
            Optional<Documentation> result = docFinder.search((ExecutableElement) holder,
                    m -> Result.fromOptional(extract(utils, m))).toOptional();
            if (result.isPresent()) {
                ExecutableElement m = result.get().method();
                tags = utils.getSeeTrees(m);
                e = m;
            }
        }
        return writer.seeTagOutput(e, tags);
    }

    private record Documentation(List<? extends SeeTree> seeTrees, ExecutableElement method) { }

    private static Optional<Documentation> extract(Utils utils, ExecutableElement method) {
        List<? extends SeeTree> tags = utils.getSeeTrees(method);
        return tags.isEmpty() ? Optional.empty() : Optional.of(new Documentation(tags, method));
    }
}
