/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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
package java.lang.constant;

/*
 * Implementation of {@code ModuleDesc}
 * @param name must have been validated
 */
record ModuleDescImpl(String name) implements ModuleDesc {

    @Override
    public String toString() {
        return String.format("ModuleDesc[%s]", name());
    }
}
