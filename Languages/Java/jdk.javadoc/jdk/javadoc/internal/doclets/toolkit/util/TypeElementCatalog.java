/*
 * Copyright (c) 2001, 2021, Oracle and/or its affiliates. All rights reserved.
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
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import jdk.javadoc.internal.doclets.toolkit.BaseConfiguration;

/**
 * This class acts as an artificial container for classes specified on the command line when
 * running Javadoc. For example, if you specify several classes from package java.lang, this class
 * will catalog those classes so that we can retrieve all of the classes from a particular package
 * later.
 *
 * <p>
 * <b>This is NOT part of any supported API. If you write code that depends on this, you do so at
 * your own risk. This code and its internal interfaces are subject to change or deletion without
 * notice.</b>
 */
public class TypeElementCatalog {

    /**
     * Stores the set of packages that the classes specified on the command line belong to. Note
     * that the default package is "".
     */
    private final SortedSet<PackageElement> packageSet;

    /**
     * Stores all classes for each package
     */
    private final Map<PackageElement, SortedSet<TypeElement>> allClasses;

    private final BaseConfiguration configuration;
    private final Utils utils;
    private final Comparator<Element> comparator;

    /**
     * Construct a new TypeElementCatalog.
     *
     * @param typeElements the array of TypeElements to catalog
     */
    public TypeElementCatalog(Iterable<TypeElement> typeElements, BaseConfiguration config) {
        this(config);
        for (TypeElement typeElement : typeElements) {
            addTypeElement(typeElement);
        }
    }

    /**
     * Construct a new TypeElementCatalog.
     *
     */
    public TypeElementCatalog(BaseConfiguration config) {
        this.configuration = config;
        this.utils = config.utils;
        comparator = utils.comparators.makeGeneralPurposeComparator();
        allClasses = new HashMap<>();
        packageSet = new TreeSet<>(comparator);
    }

    /**
     * Add the given class to the catalog.
     *
     * @param typeElement the TypeElement to add to the catalog.
     */
    public final void addTypeElement(TypeElement typeElement) {
        if (typeElement == null) {
            return;
        }
        addTypeElement(typeElement, allClasses);
    }

    /**
     * Add the given class to the given map.
     *
     * @param typeElement the class to add to the catalog.
     * @param map the Map to add the TypeElement to.
     */
    private void addTypeElement(TypeElement typeElement, Map<PackageElement, SortedSet<TypeElement>> map) {

        PackageElement pkg = utils.containingPackage(typeElement);
        if (utils.isSpecified(pkg) || configuration.getOptions().noDeprecated() && utils.isDeprecated(pkg)) {
            // No need to catalog this class if it's package is
            // specified on the command line or if -nodeprecated option is set
            return;
        }

        SortedSet<TypeElement> s = map.get(pkg);
        if (s == null) {
            packageSet.add(pkg);
            s = new TreeSet<>(comparator);
        }
        s.add(typeElement);
        map.put(pkg, s);

    }

    /**
     * Return all of the classes specified on the command-line that belong to the given package.
     *
     * @param packageElement the package to return the classes for.
     */
    public SortedSet<TypeElement> allClasses(PackageElement packageElement) {
        return utils.isSpecified(packageElement)
                ? utils.getTypeElementsAsSortedSet(utils.getEnclosedTypeElements(packageElement))
                : allClasses.getOrDefault(packageElement, Collections.emptySortedSet());
    }

    /**
     * Return all of the classes specified on the command-line that belong to the unnamed package.
     */
    public SortedSet<TypeElement> allUnnamedClasses() {
        for (PackageElement pkg : allClasses.keySet()) {
            if (pkg.isUnnamed()) {
                return allClasses.get(pkg);
            }
        }
        return new TreeSet<>(comparator);
    }

    /**
     * Return a SortedSet of packages that this catalog stores.
     */
    public SortedSet<PackageElement> packages() {
         return packageSet;
    }
}
