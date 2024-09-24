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

import java.util.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.doctree.DocTree;

/**
 * The interface for writing member summary output.
 */
public interface MemberSummaryWriter {

    /**
     * Returns the member summary header for the given class.
     *
     * @param typeElement the class the summary belongs to
     * @param content     the content to which the member summary will be added
     *
     * @return the member summary header
     */
    Content getMemberSummaryHeader(TypeElement typeElement, Content content);

    /**
     * Returns the summary table for the given class.
     *
     * @param typeElement the class the summary table belongs to
     *
     * @return the summary table
     */
    Content getSummaryTable(TypeElement typeElement);

    /**
     * Adds the member summary for the given class and member.
     *
     * @param typeElement        the class the summary belongs to
     * @param member             the member that is documented
     * @param firstSentenceTrees the tags for the sentence being documented
     */
    void addMemberSummary(TypeElement typeElement, Element member,
                          List<? extends DocTree> firstSentenceTrees);

    /**
     * Returns the inherited member summary header for the given class.
     *
     * @param typeElement the class the summary belongs to
     *
     * @return the inherited member summary header
     */
    Content getInheritedSummaryHeader(TypeElement typeElement);

    /**
     * Adds the inherited member summary for the given class and member.
     *
     * @param typeElement the class the inherited member belongs to
     * @param member the inherited member that is being documented
     * @param isFirst true if this is the first member in the list
     * @param isLast true if this is the last member in the list
     * @param content the content to which the links will be added
     */
    void addInheritedMemberSummary(TypeElement typeElement,
                                   Element member, boolean isFirst, boolean isLast,
                                   Content content);

    /**
     * Returns the inherited summary links.
     *
     * @return the inherited summary links
     */
    Content getInheritedSummaryLinks();

    /**
     * Adds the given summary to the list of summaries.
     *
     * @param summariesList the list of summaries
     * @param content       the summary
     */
    void addSummary(Content summariesList, Content content);

    /**
     * Returns the member content.
     *
     * @param memberContent the content representing the member
     *
     * @return the member content
     */
    Content getMember(Content memberContent);
}
