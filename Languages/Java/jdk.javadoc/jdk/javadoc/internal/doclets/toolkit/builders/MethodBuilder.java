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

package jdk.javadoc.internal.doclets.toolkit.builders;

import java.util.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.sun.source.doctree.DocTree;
import jdk.javadoc.internal.doclets.toolkit.BaseOptions;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.DocletException;
import jdk.javadoc.internal.doclets.toolkit.MethodWriter;
import jdk.javadoc.internal.doclets.toolkit.util.DocFinder;
import jdk.javadoc.internal.doclets.toolkit.util.DocFinder.Result;

import static jdk.javadoc.internal.doclets.toolkit.util.VisibleMemberTable.Kind.*;

/**
 * Builds documentation for a method.
 */
public class MethodBuilder extends AbstractMemberBuilder {

    /**
     * The index of the current field that is being documented at this point
     * in time.
     */
    private ExecutableElement currentMethod;

    /**
     * The writer to output the method documentation.
     */
    private final MethodWriter writer;

    /**
     * The methods being documented.
     */
    private final List<? extends Element> methods;


    /**
     * Construct a new MethodBuilder.
     *
     * @param context       the build context.
     * @param typeElement the class whose members are being documented.
     * @param writer the doclet specific writer.
     */
    private MethodBuilder(Context context,
            TypeElement typeElement,
            MethodWriter writer) {
        super(context, typeElement);
        this.writer = Objects.requireNonNull(writer);
        methods = getVisibleMembers(METHODS);
    }

    /**
     * Construct a new MethodBuilder.
     *
     * @param context       the build context.
     * @param typeElement the class whose members are being documented.
     * @param writer the doclet specific writer.
     *
     * @return an instance of a MethodBuilder.
     */
    public static MethodBuilder getInstance(Context context,
            TypeElement typeElement, MethodWriter writer) {
        return new MethodBuilder(context, typeElement, writer);
    }

    @Override
    public boolean hasMembersToDocument() {
        return !methods.isEmpty();
    }

    @Override
    public void build(Content target) throws DocletException {
        buildMethodDoc(target);
    }

    /**
     * Build the method documentation.
     *
     * @param detailsList the content to which the documentation will be added
     * @throws DocletException if there is a problem while building the documentation
     */
    protected void buildMethodDoc(Content detailsList) throws DocletException {
        if (hasMembersToDocument()) {
            Content methodDetailsHeader = writer.getMethodDetailsHeader(detailsList);
            Content memberList = writer.getMemberList();

            for (Element method : methods) {
                currentMethod = (ExecutableElement)method;
                Content methodContent = writer.getMethodHeader(currentMethod);

                buildSignature(methodContent);
                buildDeprecationInfo(methodContent);
                buildPreviewInfo(methodContent);
                buildMethodComments(methodContent);
                buildTagInfo(methodContent);

                memberList.add(writer.getMemberListItem(methodContent));
            }
            Content methodDetails = writer.getMethodDetails(methodDetailsHeader, memberList);
            detailsList.add(methodDetails);
        }
    }

    /**
     * Build the signature.
     *
     * @param methodContent the content to which the documentation will be added
     */
    protected void buildSignature(Content methodContent) {
        methodContent.add(writer.getSignature(currentMethod));
    }

    /**
     * Build the deprecation information.
     *
     * @param methodContent the content to which the documentation will be added
     */
    protected void buildDeprecationInfo(Content methodContent) {
        writer.addDeprecated(currentMethod, methodContent);
    }

    /**
     * Build the preview information.
     *
     * @param methodContent the content to which the documentation will be added
     */
    protected void buildPreviewInfo(Content methodContent) {
        writer.addPreview(currentMethod, methodContent);
    }

    /**
     * Build the comments for the method.  Do nothing if
     * {@link BaseOptions#noComment()} is set to true.
     *
     * @param methodContent the content to which the documentation will be added
     */
    protected void buildMethodComments(Content methodContent) {
        if (!options.noComment()) {
            assert utils.isMethod(currentMethod); // not all executables are methods
            var docFinder = utils.docFinder();
            Optional<ExecutableElement> r = docFinder.search(currentMethod,
                    m -> Result.fromOptional(utils.getFullBody(m).isEmpty() ? Optional.empty() : Optional.of(m))).toOptional();
            ExecutableElement method = r.orElse(currentMethod);
            TypeMirror containingType = method.getEnclosingElement().asType();
            writer.addComments(containingType, method, methodContent);
        }
    }

    /**
     * Build the tag information.
     *
     * @param methodContent the content to which the documentation will be added
     */
    protected void buildTagInfo(Content methodContent) {
        writer.addTags(currentMethod, methodContent);
    }

    /**
     * Return the method writer for this builder.
     *
     * @return the method writer for this builder.
     */
    public MethodWriter getWriter() {
        return writer;
    }
}
