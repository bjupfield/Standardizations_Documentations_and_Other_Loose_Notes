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

package jdk.javadoc.internal.doclets.toolkit;

import javax.lang.model.element.Element;

/**
 * The interface for writing annotation type required member output.
 */
public interface AnnotationTypeMemberWriter extends MemberWriter {

    /**
     * Adds the annotation type member header.
     *
     * @return the content for the member header
     */
    Content getMemberHeader();

    /**
     * Adds the annotation type details marker.
     *
     * @param memberDetails the content representing details marker
     */
    void addAnnotationDetailsMarker(Content memberDetails);

    /**
     * Adds the annotation type details header.
     *
     * @return the content for the annotation details header
     */
    Content getAnnotationDetailsHeader();

    /**
     * Gets the annotation type documentation header.
     *
     * @param member the annotation type being documented
     * @return the content for the annotation type documentation header
     */
    Content getAnnotationHeaderContent(Element member);

    /**
     * Gets the annotation type details.
     *
     * @param annotationDetailsHeader the content representing annotation type details header
     * @param annotationDetails the content representing annotation type details
     * @return the annotation type details
     */
    Content getAnnotationDetails(Content annotationDetailsHeader, Content annotationDetails);

    /**
     * {@return the signature for the specified member}
     *
     * @param member the member being documented
     */
    Content getSignature(Element member);

    /**
     * Adds the deprecated output for the given member.
     *
     * @param member the member being documented
     * @param target the content to which the deprecated information will be added
     */
    void addDeprecated(Element member, Content target);

    /**
     * Adds the preview output for the given member.
     *
     * @param member the member being documented
     * @param content the content to which the preview information will be added
     */
    void addPreview(Element member, Content content);

    /**
     * Adds the comments for the given member.
     *
     * @param member the member being documented
     * @param annotationContent the content to which the comments will be added
     */
    void addComments(Element member, Content annotationContent);

    /**
     * Adds the tags for the given member.
     *
     * @param member the member being documented
     * @param annotationContent the content to which the tags will be added
     */
    void addTags(Element member, Content annotationContent);

    /**
     * Adds the default value documentation if the member has one.
     *
     * @param member the member being documented
     * @param annotationContent the content to which the default value will be added
     */
    void addDefaultValueInfo(Element member, Content annotationContent);
}
