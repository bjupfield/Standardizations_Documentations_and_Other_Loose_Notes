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
package jdk.jpackage.internal;

import java.util.Map;
import static jdk.jpackage.internal.StandardBundlerParam.APP_NAME;
import static jdk.jpackage.internal.StandardBundlerParam.DESCRIPTION;

class LauncherAsService {

    LauncherAsService(String name, Map<String, Object> mainParams,
            OverridableResource resource) {
        if (name == null || APP_NAME.fetchFrom(mainParams).equals(name)) {
            // Main launcher
            name = APP_NAME.fetchFrom(mainParams);
            this.description = DESCRIPTION.fetchFrom(mainParams);
        } else {
            // Additional launcher
            this.description = String.format("%s (%s)", DESCRIPTION.fetchFrom(
                    mainParams), name);
        }

        this.name = name;
        this.resource = resource;
        resource.addSubstitutionDataEntry("SERVICE_DESCRIPTION", description);
    }

    protected OverridableResource getResource() {
        return resource;
    }

    protected String getName() {
        return name;
    }

    protected String getDescription() {
        return description;
    }

    private final String name;
    private final String description;
    private final OverridableResource resource;
}
