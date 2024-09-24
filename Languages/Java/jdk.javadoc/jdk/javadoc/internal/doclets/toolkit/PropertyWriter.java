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

import javax.lang.model.element.ExecutableElement;

/**
 * The interface for writing property output.
 */
public interface PropertyWriter extends MemberWriter {

    /**
     * Get the property details header.
     *
     * @param memberDetails the content representing member details
     * @return the property details header
     */
    Content getPropertyDetailsHeader(Content memberDetails);

    /**
     * Get the property documentation header.
     *
     * @param property the property being documented
     * @return the property documentation header
     */
    Content getPropertyHeaderContent(ExecutableElement property);

    /**
     * Get the signature for the given property.
     *
     * @param property the property being documented
     * @return the property signature
     */
    Content getSignature(ExecutableElement property);

    /**
     * Add the deprecated output for the given property.
     *
     * @param property the property being documented
     * @param propertyContent content to which the deprecated information will be added
     */
    void addDeprecated(ExecutableElement property, Content propertyContent);

    /**
     * Add the preview output for the given member.
     *
     * @param member the member being documented
     * @param content the content to which the preview information will be added
     */
    void addPreview(ExecutableElement member, Content content);

    /**
     * Add the comments for the given property.
     *
     * @param property the property being documented
     * @param propertyContent the content to which the comments will be added
     */
    void addComments(ExecutableElement property, Content propertyContent);

    /**
     * Add the tags for the given property.
     *
     * @param property the property being documented
     * @param propertyContent the content to which the tags will be added
     */
    void addTags(ExecutableElement property, Content propertyContent);

    /**
     * Get the property details.
     *
     * @param memberDetailsHeader the content representing member details header
     * @param memberDetails the content representing member details
     * @return the property details
     */
    Content getPropertyDetails(Content memberDetailsHeader, Content memberDetails);
}
