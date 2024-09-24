/*
 * Copyright (c) 1998, 2022, Oracle and/or its affiliates. All rights reserved.
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

import java.util.*;

import javax.lang.model.element.Element;

import jdk.javadoc.internal.doclets.toolkit.BaseConfiguration;

/**
 * Build list of all the deprecated packages, classes, constructors, fields and methods.
 */
public class DeprecatedAPIListBuilder extends SummaryAPIListBuilder {

    private SortedSet<Element> forRemoval;
    public final List<String> releases;
    private final Set<String> foundReleases;

    /**
     * Constructor.
     *
     * @param configuration the current configuration of the doclet
     * @param since list of releases passed via <code>--since</code> option
     */
    public DeprecatedAPIListBuilder(BaseConfiguration configuration, List<String> since) {
        super(configuration, configuration.utils::isDeprecated);
        this.foundReleases = new HashSet<>();
        buildSummaryAPIInfo();
        // The releases list is set to the intersection of releases defined via `--since` option
        // and actually occurring values of `Deprecated.since` in documented deprecated elements.
        // If there are `Deprecated.since` values not contained in the `--since` option list
        // an empty string is added to the releases list which causes the writer to generate
        // a checkbox for other (unlisted) releases.
        List<String> releases = new ArrayList<>(since);
        if (!releases.isEmpty()) {
            releases.retainAll(foundReleases);
            if (!releases.containsAll(foundReleases)) {
                // Empty string is added for other releases, including the default value ""
                releases.add("");
            }
        }
        this.releases = Collections.unmodifiableList(releases);
    }

    public SortedSet<Element> getForRemoval() {
        if (forRemoval == null) {
            forRemoval = createSummarySet();
        }
        return forRemoval;
    }

    @Override
    protected void handleElement(Element e) {
        foundReleases.add(utils.getDeprecatedSince(e));
        if (utils.isDeprecatedForRemoval(e)) {
            getForRemoval().add(e);
        }
    }
}
