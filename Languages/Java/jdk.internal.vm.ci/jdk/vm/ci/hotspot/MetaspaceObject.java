/*
 * Copyright (c) 2015, 2019, Oracle and/or its affiliates. All rights reserved.
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
 */
package jdk.vm.ci.hotspot;

/**
 * The marker interface for an object which wraps a HotSpot Metaspace object.
 */
interface MetaspaceObject {

    /**
     * Gets the raw pointer to the {@code Metaspace} object.
     *
     * @return a {@code Klass*}, {@code Method*} or {@code ConstantPool*} value
     */
    long getMetaspacePointer();
}
