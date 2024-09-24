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

import javax.lang.model.element.VariableElement;

/**
 * The interface for writing field output.
 */
public interface FieldWriter extends MemberWriter {

    /**
     * Get the field details header.
     *
     * @param content the content representing member details
     * @return the field details header
     */
    Content getFieldDetailsHeader(Content content);

    /**
     * Get the field documentation header.
     *
     * @param field the constructor being documented
     * @return the field documentation header
     */
    Content getFieldHeaderContent(VariableElement field);

    /**
     * Get the signature for the given field.
     *
     * @param field the field being documented
     * @return the field signature
     */
    Content getSignature(VariableElement field);

    /**
     * Add the deprecated output for the given field.
     *
     * @param field the field being documented
     * @param fieldContent the content to which the deprecated information will be added
     */
    void addDeprecated(VariableElement field, Content fieldContent);

    /**
     * Adds the preview output for the given member.
     *
     * @param member the member being documented
     * @param content the content to which the preview information will be added
     */
    void addPreview(VariableElement member, Content content);

    /**
     * Add the comments for the given field.
     *
     * @param field the field being documented
     * @param fieldContent the content to which the comments will be added
     */
    void addComments(VariableElement field, Content fieldContent);

    /**
     * Add the tags for the given field.
     *
     * @param field the field being documented
     * @param fieldContent the content to which the tags will be added
     */
    void addTags(VariableElement field, Content fieldContent);

    /**
     * Get the field details.
     *
     * @param memberDetailsHeaderContent the content representing member details header
     * @param memberContent the content representing member details
     * @return the field details
     */
    Content getFieldDetails(Content memberDetailsHeaderContent, Content memberContent);

    /**
     * Gets the member header.
     *
     * @return the member header
     */
    Content getMemberHeader();
}
