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

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import jdk.javadoc.internal.doclets.toolkit.util.DocFileIOException;

/**
 * The interface for writing constants summary output.
 */
public interface ConstantsSummaryWriter {

    /**
     * Get the header for the constant summary documentation.
     *
     * @return header that needs to be added to the documentation
     */
    Content getHeader();

    /**
     * Get the header for the constant content list.
     *
     * @return content header that needs to be added to the documentation
     */
    Content getContentsHeader();

    /**
     * Adds the given package name link to the constant content list.
     *
     * @param abbrevPackageName the abbreviated package name
     * @param content       the content to which the link will be added
     */
    void addLinkToPackageContent(String abbrevPackageName, Content content);

    /**
     * Add the content list to the documentation.
     *
     * @param content the content that will be added to the list
     */
    void addContentsList(Content content);

    /**
     * Get the constant summaries for the document.
     *
     * @return constant summaries header to be added to the documentation
     */
    Content getConstantSummaries();

    /**
     * Adds a header for the given abbreviated package name.
     *
     * @param abbrevPackageName  the abbreviated package name
     * @param toContent the summaries documentation
     */
    void addPackageGroup(String abbrevPackageName, Content toContent);

    /**
     * Get the class summary header for the constants summary.
     *
     * @return the header content for the class constants summary
     */
    Content getClassConstantHeader();

    /**
     * Add the content list to the documentation summaries.
     *
     * @param fromClassConstant the class constant content that will be added to the list
     */
    void addClassConstant(Content fromClassConstant);

    /**
     * Adds the constant member table to the documentation.
     *
     * @param typeElement the class whose constants are being documented.
     * @param fields the constants being documented.
     * @param target the content to which the constant member
     *               table content will be added
     */
    void addConstantMembers(TypeElement typeElement, Collection<VariableElement> fields,
                            Content target);

    /**
     * Add the summaries list to the content.
     *
     * @param content the summaries content that will be added to the list
     */
    void addConstantSummaries(Content content);

    /**
     * Adds the footer for the summary documentation.
     */
    void addFooter();

    /**
     * Print the constants summary document.
     *
     * @param content the content which should be printed
     * @throws DocFileIOException if there is a problem while writing the document
     */
    void printDocument(Content content) throws DocFileIOException;
}
