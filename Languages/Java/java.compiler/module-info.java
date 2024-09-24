/*
 * Copyright (c) 2014, 2021, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Defines the {@index "Language Model"}, {@index "Annotation Processing"}, and
 * {@index "Java Compiler"} APIs.
 *
 * <p>
 * These APIs model declarations and types of the Java programming language,
 * and define interfaces for tools such as compilers which can be invoked
 * from a program.
 * <p>
 * This module is upgradeable.
 *
 * @moduleGraph
 * @since 9
 */
module java.compiler {
    exports javax.annotation.processing;
    exports javax.lang.model;
    exports javax.lang.model.element;
    exports javax.lang.model.type;
    exports javax.lang.model.util;
    exports javax.tools;

    uses javax.tools.DocumentationTool;
    uses javax.tools.JavaCompiler;
}

