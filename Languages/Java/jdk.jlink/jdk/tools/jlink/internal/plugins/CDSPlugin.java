/*
 * Copyright (c) 2021, 2023, Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import jdk.internal.util.Architecture;
import jdk.internal.util.OperatingSystem;
import jdk.tools.jlink.internal.ExecutableImage;
import jdk.tools.jlink.internal.Platform;
import jdk.tools.jlink.internal.PostProcessor;
import jdk.tools.jlink.plugin.PluginException;
import jdk.tools.jlink.plugin.ResourcePool;
import jdk.tools.jlink.plugin.ResourcePoolBuilder;

/**
 *
 * CDS plugin
 */
public final class CDSPlugin extends AbstractPlugin implements PostProcessor {
    private static final String NAME = "generate-cds-archive";
    private Platform targetPlatform;
    private Platform runtimePlatform;

    public CDSPlugin() {
        super(NAME);
    }


    private String javaExecutableName() {
        if (targetPlatform.os() == OperatingSystem.WINDOWS) {
            return "java.exe";
        } else {
            return "java";
        }
    }

    private void generateCDSArchive(ExecutableImage image, boolean noCoops) {
        List<String> javaCmd = new ArrayList<String>();
        Path javaPath = image.getHome().resolve("bin").resolve(javaExecutableName());
        if (!Files.exists(javaPath)) {
            throw new PluginException("Cannot find java executable at: " + javaPath.toString());
        }
        javaCmd.add(javaPath.toString());
        javaCmd.add("-Xshare:dump");
        String archiveMsg = "CDS";
        if (noCoops) {
            javaCmd.add("-XX:-UseCompressedOops");
            archiveMsg += "-NOCOOPS";
        }
        ProcessBuilder builder = new ProcessBuilder(javaCmd);
        int status = -1;
        try {
            Process p = builder.inheritIO().start();
            status = p.waitFor();
        } catch (InterruptedException | IOException e) {
            throw new PluginException(e);
        }

        if (status != 0) {
            throw new PluginException("Failed creating " + archiveMsg + " archive!");
        }
    }

    @Override
    public List<String> process(ExecutableImage image) {
        targetPlatform = image.getTargetPlatform();
        runtimePlatform = Platform.runtime();

        if (!targetPlatform.equals(runtimePlatform)) {
            throw new PluginException("Cannot generate CDS archives: target image platform " +
                    targetPlatform.toString() + " is different from runtime platform " +
                    runtimePlatform.toString());
        }

        Path classListPath = image.getHome().resolve("lib").resolve("classlist");
        if (Files.exists(classListPath)) {
            generateCDSArchive(image,false);

            // The targetPlatform is the same as the runtimePlatform.
            // For a 64-bit platform, generate the non-compressed oop CDS archive
            if (Architecture.is64bit()) {
                generateCDSArchive(image,true);
            }
            System.out.println("Created CDS archive successfully");
        } else {
            throw new PluginException("Cannot generate CDS archives: classlist not found: " +
                                      classListPath.toString());
        }
        return null;
    }

    @Override
    public Category getType() {
        return Category.PROCESSOR;
    }

    @Override
    public ResourcePool transform(ResourcePool in, ResourcePoolBuilder out) {
        return in;
    }
}
