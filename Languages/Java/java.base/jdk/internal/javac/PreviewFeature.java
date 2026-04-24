/*
 * Copyright (c) 2019, 2023, Oracle and/or its affiliates. All rights reserved.
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

package jdk.internal.javac;

import java.lang.annotation.*;

/**
 * Indicates the API declaration in question is associated with a
 * <em>preview feature</em>. See JEP 12: "Preview Language and VM
 * Features" (https://openjdk.org/jeps/12).
 *
 * Note this internal annotation is handled specially by the javac compiler.
 * To work properly with {@code --release older-release}, it requires special
 * handling in {@code make/langtools/src/classes/build/tools/symbolgenerator/CreateSymbols.java}
 * and {@code src/jdk.compiler/share/classes/com/sun/tools/javac/jvm/ClassReader.java}.
 *
 * @since 14
 */
// Match the meaningful targets of java.lang.Deprecated, omit local
// variables and parameter declarations
@Target({ElementType.METHOD,
         ElementType.CONSTRUCTOR,
         ElementType.FIELD,
         ElementType.PACKAGE,
         ElementType.MODULE,
         ElementType.TYPE})
 // CLASS retention will hopefully be sufficient for the purposes at hand
@Retention(RetentionPolicy.CLASS)
// *Not* @Documented
public @interface PreviewFeature {
    /**
     * Name of the preview feature the annotated API is associated
     * with.
     */
    public Feature feature();

    public boolean reflective() default false;

    /**
     * Enum of preview features in the current release.
     * Values should be annotated with the feature's {@code JEP}.
     */
    public enum Feature {
        // not used
        VIRTUAL_THREADS,
        @JEP(number=442, title="Foreign Function & Memory API", status="Third Preview")
        FOREIGN,
        @JEP(number=430, title="String Templates")
        STRING_TEMPLATES,
        @JEP(number=443, title="Unnamed Patterns and Variables")
        UNNAMED,
        @JEP(number=445, title="Unnamed Classes and Instance Main Methods")
        UNNAMED_CLASSES,
        @JEP(number=446, title="Scoped Values", status="Preview")
        SCOPED_VALUES,
        @JEP(number=453, title="Structured Concurrency", status="Preview")
        STRUCTURED_CONCURRENCY,
        /**
         * A key for testing.
         */
        @JEP(number=0, title="Test Feature")
        TEST,
        ;
    }

    /**
     * Annotation identifying the JEP associated with a preview feature.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.CLASS)
    @interface JEP {
        /** JEP number */
        int number() default 0;
        /** JEP title in plain text */
        String title();
        /** JEP status such as "Preview", "Second Preview", etc */
        String status() default "Preview";
    }
}
