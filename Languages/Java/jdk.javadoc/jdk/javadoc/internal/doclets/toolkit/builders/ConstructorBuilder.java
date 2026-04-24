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

import jdk.javadoc.internal.doclets.toolkit.BaseOptions;
import jdk.javadoc.internal.doclets.toolkit.ConstructorWriter;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.DocletException;

import static jdk.javadoc.internal.doclets.toolkit.util.VisibleMemberTable.Kind.*;

/**
 * Builds documentation for a constructor.
 */
public class ConstructorBuilder extends AbstractMemberBuilder {

    /**
     * The current constructor that is being documented at this point in time.
     */
    private ExecutableElement currentConstructor;

    /**
     * The writer to output the constructor documentation.
     */
    private final ConstructorWriter writer;

    /**
     * The constructors being documented.
     */
    private final List<? extends Element> constructors;

    /**
     * Construct a new ConstructorBuilder.
     *
     * @param context  the build context.
     * @param typeElement the class whose members are being documented.
     * @param writer the doclet specific writer.
     */
    private ConstructorBuilder(Context context,
            TypeElement typeElement,
            ConstructorWriter writer) {
        super(context, typeElement);
        this.writer = Objects.requireNonNull(writer);
        constructors = getVisibleMembers(CONSTRUCTORS);
        for (Element ctor : constructors) {
            if (utils.isProtected(ctor) || utils.isPrivate(ctor)) {
                writer.setFoundNonPubConstructor(true);
            }
        }
    }

    /**
     * Construct a new ConstructorBuilder.
     *
     * @param context  the build context.
     * @param typeElement the class whose members are being documented.
     * @param writer the doclet specific writer.
     * @return the new ConstructorBuilder
     */
    public static ConstructorBuilder getInstance(Context context,
            TypeElement typeElement, ConstructorWriter writer) {
        return new ConstructorBuilder(context, typeElement, writer);
    }

    @Override
    public boolean hasMembersToDocument() {
        return !constructors.isEmpty();
    }

    /**
     * Return the constructor writer for this builder.
     *
     * @return the constructor writer for this builder.
     */
    public ConstructorWriter getWriter() {
        return writer;
    }

    @Override
    public void build(Content target) throws DocletException {
        buildConstructorDoc(target);
    }

    /**
     * Build the constructor documentation.
     *
     * @param target the content to which the documentation will be added
     * @throws DocletException if there is a problem while building the documentation
     */
    protected void buildConstructorDoc(Content target) throws DocletException {
        if (hasMembersToDocument()) {
            Content constructorDetailsHeader = writer.getConstructorDetailsHeader(target);
            Content memberList = writer.getMemberList();

            for (Element constructor : constructors) {
                currentConstructor = (ExecutableElement)constructor;
                Content constructorContent = writer.getConstructorHeaderContent(currentConstructor);

                buildSignature(constructorContent);
                buildDeprecationInfo(constructorContent);
                buildPreviewInfo(constructorContent);
                buildConstructorComments(constructorContent);
                buildTagInfo(constructorContent);

                memberList.add(writer.getMemberListItem(constructorContent));
            }
            Content constructorDetails = writer.getConstructorDetails(constructorDetailsHeader, memberList);
            target.add(constructorDetails);
        }
    }

    /**
     * Build the signature.
     *
     * @param constructorContent the content to which the documentation will be added
     */
    protected void buildSignature(Content constructorContent) {
        constructorContent.add(writer.getSignature(currentConstructor));
    }

    /**
     * Build the deprecation information.
     *
     * @param constructorContent the content to which the documentation will be added
     */
    protected void buildDeprecationInfo(Content constructorContent) {
        writer.addDeprecated(currentConstructor, constructorContent);
    }

    /**
     * Build the preview information.
     *
     * @param constructorContent the content to which the documentation will be added
     */
    protected void buildPreviewInfo(Content constructorContent) {
        writer.addPreview(currentConstructor, constructorContent);
    }

    /**
     * Build the comments for the constructor.  Do nothing if
     * {@link BaseOptions#noComment()} is set to true.
     *
     * @param constructorContent the content to which the documentation will be added
     */
    protected void buildConstructorComments(Content constructorContent) {
        if (!options.noComment()) {
            writer.addComments(currentConstructor, constructorContent);
        }
    }

    /**
     * Build the tag information.
     *
     * @param constructorContent the content to which the documentation will be added
     */
    protected void buildTagInfo(Content constructorContent) {
        writer.addTags(currentConstructor, constructorContent);
    }
}
