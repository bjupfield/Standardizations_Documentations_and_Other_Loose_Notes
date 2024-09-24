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
 */
package jdk.vm.ci.meta;

/**
 * Represents an enum constant within {@link AnnotationData}.
 */
public final class EnumData {
    private final JavaType type;
    private final String name;

    /**
     * Creates an enum constant.
     *
     * @param type the {@linkplain Enum enum type}
     * @param name the {@linkplain Enum#name() name} of the enum
     */
    public EnumData(JavaType type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Gets the {@linkplain Enum enum type}.
     */
    public JavaType getEnumType() {
        return type;
    }

    /**
     * Gets the {@linkplain Enum#name() name} of the enum.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof EnumData) {
            EnumData that = (EnumData) obj;
            return this.type.equals(that.type) && this.name.equals(that.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.type.hashCode() ^ this.name.hashCode();
    }
}
