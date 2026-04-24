/*
 * Copyright (c) 2018, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.toolkit.util;

import jdk.javadoc.internal.doclets.toolkit.BaseConfiguration;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the visible member table for each type element.
 */
public class VisibleMemberCache {
    private final Map<TypeElement, VisibleMemberTable> cache;
    private final BaseConfiguration configuration;

    public VisibleMemberCache(BaseConfiguration configuration) {
        this.configuration = configuration;
        cache = new HashMap<>();
    }

    public VisibleMemberTable getVisibleMemberTable(TypeElement te) {
        return cache.computeIfAbsent(te, t -> new VisibleMemberTable(t, configuration, this));
    }
}
