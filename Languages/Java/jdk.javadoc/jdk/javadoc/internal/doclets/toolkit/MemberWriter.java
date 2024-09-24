/*
 * Copyright (c) 2020, 2022, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Common behavior for writing members of a type.
 */
public interface MemberWriter {

    /**
     * {@return a list to add member items to}
     *
     * @see #getMemberListItem(Content)
     */
    Content getMemberList();

    /**
     * {@return a member item}
     *
     * @param member the member to represent as an item
     * @see #getMemberList()
     */
    Content getMemberListItem(Content member);
}
