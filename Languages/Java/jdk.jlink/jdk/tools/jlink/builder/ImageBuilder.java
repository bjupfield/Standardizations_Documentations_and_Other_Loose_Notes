/*
 * Copyright (c) 2015, 2023, Oracle and/or its affiliates. All rights reserved.
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
package jdk.tools.jlink.builder;

import java.io.DataOutputStream;
import java.util.Properties;

import jdk.tools.jlink.internal.ExecutableImage;
import jdk.tools.jlink.internal.Platform;
import jdk.tools.jlink.plugin.PluginException;
import jdk.tools.jlink.plugin.ResourcePool;

/**
 * Implement this interface to develop your own image layout. First the jimage
 * is written onto the output stream returned by getOutputStream then storeFiles
 * is called.
 */
public interface ImageBuilder {

    /**
     * Store the external files.
     *
     * @param content Pool of module content.
     * @param release the release properties
     * @throws PluginException
     */
    public default void storeFiles(ResourcePool content, Properties release) {
        storeFiles(content);
    }

    /**
     * Store the external files.
     *
     * @param content Pool of module content.
     * @throws PluginException
     */
    public default void storeFiles(ResourcePool content) {
        throw new UnsupportedOperationException("storeFiles");
    }

    /**
     * The OutputStream to store the jimage file.
     *
     * @return The output stream
     * @throws PluginException
     */
    public DataOutputStream getJImageOutputStream();

    /**
     * Gets the executable image that is generated.
     *
     * @return The executable image.
     * @throws PluginException
     */
    public ExecutableImage getExecutableImage();

    /**
     * Gets the platform of the image.
     *
     * @return {@code Platform} object representing the platform of the image
     * @throws UnsupportedOperationException if this method is not implemented by the ImageBuilder
     */
    public default Platform getTargetPlatform() {
        throw new UnsupportedOperationException("Builder does not define getTargetPlatform");
    }
}
