/*
 * Copyright (c) 2017, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.jpackage.internal;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.spi.ToolProvider;

/**
 * JPackageToolProvider
 *
 * This is the ToolProvider implementation exported
 * to java.util.spi.ToolProvider and ultimately javax.tools.ToolProvider
 */
public class JPackageToolProvider implements ToolProvider {

    public String name() {
        return "jpackage";
    }

    public Optional<String> description() {
        return Optional.of(jdk.jpackage.main.Main.I18N.getString("jpackage.description"));
    }

    public synchronized int run(
            PrintWriter out, PrintWriter err, String... args) {
        try {
            return new jdk.jpackage.main.Main().execute(out, err, args);
        } catch (RuntimeException re) {
            Log.fatalError(re.getMessage());
            Log.verbose(re);
            return 1;
        }
    }
}
