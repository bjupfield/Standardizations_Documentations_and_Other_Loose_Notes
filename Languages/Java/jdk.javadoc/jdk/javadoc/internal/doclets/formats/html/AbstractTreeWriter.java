/*
 * Copyright (c) 1998, 2023, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.formats.html;

import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlTree;
import jdk.javadoc.internal.doclets.formats.html.markup.TagName;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.ClassTree;
import jdk.javadoc.internal.doclets.toolkit.util.ClassTree.Hierarchy;
import jdk.javadoc.internal.doclets.toolkit.util.DocPath;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Abstract class to print the class hierarchy page for all the Classes. This
 * is subclassed by {@link PackageTreeWriter} and {@link TreeWriter} to
 * generate the Package Tree and global Tree(for all the classes and packages)
 * pages.
 */
public abstract class AbstractTreeWriter extends HtmlDocletWriter {

    /**
     * The class and interface tree built by using {@link ClassTree}
     */
    protected final ClassTree classTree;

    /**
     * Constructor. This constructor will be used while generating global tree file "overview-tree.html".
     *
     * @param configuration  The current configuration
     * @param filename   File to be generated.
     * @param classTree  Tree built by {@link ClassTree}.
     */
    protected AbstractTreeWriter(HtmlConfiguration configuration,
                                 DocPath filename, ClassTree classTree) {
        super(configuration, filename);
        this.classTree = classTree;
    }

    /**
     * Add each level of the class tree. For each subclass or
     * subinterface indents the next level information.
     * Recurses itself to add subclasses info.
     *
     * @param parent the superclass or superinterface of the sset
     * @param collection  a collection of the subclasses at this level
     * @param hierarchy the hierarchy for which we are generating a tree
     * @param content the content to which the level information will be added
     */
    protected void addLevelInfo(TypeElement parent, Collection<TypeElement> collection,
                                Hierarchy hierarchy, Content content) {
        if (!collection.isEmpty()) {
            var ul = new HtmlTree(TagName.UL);
            for (TypeElement local : collection) {
                var li = new HtmlTree(TagName.LI);
                li.setStyle(HtmlStyle.circle);
                addPartialInfo(local, li);
                addExtendsImplements(parent, local, li);
                addLevelInfo(local, hierarchy.subtypes(local), hierarchy, li);   // Recurse
                ul.add(li);
            }
            content.add(ul);
        }
    }

    /**
     * Adds a class or interface hierarchy with a given heading to given content.
     *
     * @param hierarchy the hierarchy to add
     * @param heading   the heading
     * @param content   the content to which to add the hierarchy
     */
    protected void addTree(Hierarchy hierarchy, String heading, Content content) {
        SortedSet<TypeElement> roots = hierarchy.roots();
        if (!roots.isEmpty()) {
            TypeElement firstTypeElement = roots.first();
            Content headingContent = contents.getContent(heading);
            var sectionHeading = HtmlTree.HEADING_TITLE(Headings.CONTENT_HEADING,
                    headingContent);
            var section = HtmlTree.SECTION(HtmlStyle.hierarchy, sectionHeading);
            addLevelInfo(!utils.isPlainInterface(firstTypeElement) ? firstTypeElement : null,
                    roots, hierarchy, section);
            content.add(section);
        }
    }

    /**
     * Add information regarding the classes which this class extends or implements.
     *
     * @param parent the parent class of the class being documented
     * @param typeElement the TypeElement under consideration
     * @param content the content to which the information will be added
     */
    protected void addExtendsImplements(TypeElement parent,
                                        TypeElement typeElement,
                                        Content content)
    {
        SortedSet<TypeElement> interfaces = new TreeSet<>(comparators.makeGeneralPurposeComparator());
        typeElement.getInterfaces().forEach(t -> interfaces.add(utils.asTypeElement(t)));
        if (interfaces.size() > (utils.isPlainInterface(typeElement) ? 1 : 0)) {
            boolean isFirst = true;
            for (TypeElement intf : interfaces) {
                if (parent != intf) {
                    if (utils.isPublic(intf) || utils.isLinkable(intf)) {
                        if (isFirst) {
                            isFirst = false;
                            if (utils.isPlainInterface(typeElement)) {
                                content.add(" (");
                                content.add(contents.also);
                                content.add(" extends ");
                            } else {
                                content.add(" (implements ");
                            }
                        } else {
                            content.add(", ");
                        }
                        addPreQualifiedClassLink(HtmlLinkInfo.Kind.SHOW_TYPE_PARAMS, intf, content);
                    }
                }
            }
            if (!isFirst) {
                content.add(")");
            }
        }
    }

    /**
     * Add information about the class kind, if it's a "class" or "interface".
     *
     * @param typeElement the class being documented
     * @param content the content to which the information will be added
     */
    protected void addPartialInfo(TypeElement typeElement, Content content) {
        addPreQualifiedStrongClassLink(HtmlLinkInfo.Kind.SHOW_TYPE_PARAMS, typeElement, content);
    }
}
