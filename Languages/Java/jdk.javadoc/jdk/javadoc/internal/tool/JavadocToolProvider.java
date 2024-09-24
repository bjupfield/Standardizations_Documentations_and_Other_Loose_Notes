/*
 * Copyright (c) 2016, 2022, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JavacMessages;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.spi.ToolProvider;

/**
 * An implementation of the {@link java.util.spi.ToolProvider ToolProvider} SPI,
 * providing access to JDK documentation tool, javadoc.
 *
 * @since 9
 */
// This is currently a stand-alone top-level class so that it can easily be excluded
// from interims builds of javadoc, used while building JDK.
public class JavadocToolProvider implements ToolProvider {

    @Override
    public String name() {
        return "javadoc";
    }

    // @Override - commented out due to interim builds of javadoc with JDKs < 19.
    public Optional<String> description() {
        JavacMessages messages = JavacMessages.instance(new Context());
        messages.add(locale -> ResourceBundle.getBundle("jdk.javadoc.internal.tool.resources.javadoc", locale));
        return Optional.of(messages.getLocalizedString("javadoc.description"));
    }

    @Override
    public int run(PrintWriter out, PrintWriter err, String... args) {
        return Main.execute(args, out, err);
    }
}
