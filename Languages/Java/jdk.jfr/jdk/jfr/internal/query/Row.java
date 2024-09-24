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
package jdk.jfr.internal.query;

import java.util.Arrays;

final class Row {
    private final Object[] values;
    private final String[] texts;

    public Row(int size) {
        values = new Object[size];
        texts = new String[size];
    }

    public Object getValue(int index) {
        return values[index];
    }

    public void putValue(int index, Object o) {
        values[index] = o;
    }

    public String getText(int index) {
        return texts[index];
    }

    public void putText(int index, String text) {
        texts[index] = text;
    }

    @Override
    public String toString() {
        return Arrays.asList(values).toString();
    }
}
