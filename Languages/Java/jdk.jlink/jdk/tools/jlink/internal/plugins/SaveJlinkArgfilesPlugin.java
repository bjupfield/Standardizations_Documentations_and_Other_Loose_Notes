/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.tools.jlink.internal.plugins;

import static jdk.tools.jlink.internal.JlinkTask.OPTIONS_RESOURCE;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jdk.tools.jlink.plugin.PluginException;
import jdk.tools.jlink.plugin.ResourcePool;
import jdk.tools.jlink.plugin.ResourcePoolBuilder;
import jdk.tools.jlink.plugin.ResourcePoolEntry;

/**
 * Saves the arguments in the specified argument files to a resource that's read
 * by jlink in the output image. The saved arguments are prepended to the arguments
 * specified on the jlink command line.
 */
public final class SaveJlinkArgfilesPlugin extends AbstractPlugin {

    public SaveJlinkArgfilesPlugin() {
        super("save-jlink-argfiles");
    }

    private List<String> argfiles = new ArrayList<>();

    @Override
    public Category getType() {
        return Category.ADDER;
    }

    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasRawArgument() {
        return true;
    }

    @Override
    public void configure(Map<String, String> config) {
        var v = config.get(getName());

        if (v == null)
            throw new AssertionError();

        for (String argfile : v.split(File.pathSeparator)) {
            argfiles.add(readArgfile(argfile));
        }
    }

    private static String readArgfile(String argfile) {
        try {
            return Files.readString(Path.of(argfile));
        } catch (IOException e) {
            throw new PluginException("Argfile " + argfile + " is not readable");
        }
    }

    @Override
    public ResourcePool transform(ResourcePool in, ResourcePoolBuilder out) {
        in.transformAndCopy(Function.identity(), out);
        if (!in.moduleView().findModule("jdk.jlink").isPresent()) {
            throw new PluginException("--save-jlink-argfiles requires jdk.jlink to be in the output image");
        }
        byte[] savedOptions = argfiles.stream()
                                      .collect(Collectors.joining("\n"))
                                      .getBytes(StandardCharsets.UTF_8);
        out.add(ResourcePoolEntry.create("/jdk.jlink/" + OPTIONS_RESOURCE,
                                         savedOptions));
        return out.build();
    }
}
