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
 * Represents an error constant within {@link AnnotationData}.
 *
 * Similar to {@code sun.reflect.annotation.ExceptionProxy}.
 */
public final class ErrorData {
    private final String description;

    /**
     * Creates an error constant.
     *
     * @param description description of the error
     */
    public ErrorData(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ErrorData) {
            ErrorData that = (ErrorData) obj;
            return this.description.equals(that.description);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }
}
