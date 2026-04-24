/*
 * Copyright (c) 2001, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.tool;

import java.util.EnumSet;

import javax.tools.JavaFileObject;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.code.ClassFinder;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Context.Factory;

/** Javadoc uses an extended class finder that records package.html entries
 */
public class JavadocClassFinder extends ClassFinder {

    public static JavadocClassFinder instance(Context context) {
        ClassFinder instance = context.get(classFinderKey);
        if (instance == null)
            instance = new JavadocClassFinder(context);
        return (JavadocClassFinder)instance;
    }

    public static void preRegister(Context context) {
        context.put(classFinderKey, (Factory<ClassFinder>)JavadocClassFinder::new);
    }

    private ToolEnvironment toolEnv;
    private EnumSet<JavaFileObject.Kind> all = EnumSet.of(JavaFileObject.Kind.CLASS,
                                                          JavaFileObject.Kind.SOURCE,
                                                          JavaFileObject.Kind.HTML);
    private EnumSet<JavaFileObject.Kind> noSource = EnumSet.of(JavaFileObject.Kind.CLASS,
                                                               JavaFileObject.Kind.HTML);
    private JavacTrees jctrees;

    private final JavacTrees trees;

    public JavadocClassFinder(Context context) {
        super(context);
        toolEnv = ToolEnvironment.instance(context);
        preferSource = true;
        trees = JavacTrees.instance(context);
    }

    /**
     * Override getPackageFileKinds to include search for package.html
     */
    @Override
    protected EnumSet<JavaFileObject.Kind> getPackageFileKinds() {
        return toolEnv.docClasses ? noSource : all;
    }

    /**
     * Override extraFileActions to check for package documentation
     */
    @Override
    protected void extraFileActions(PackageSymbol pack, JavaFileObject fo) {
        if (fo.isNameCompatible("package", JavaFileObject.Kind.HTML)) {
            pack.sourcefile = fo;
        }
    }
}
