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

import java.util.ArrayList;
import java.util.List;

import jdk.jfr.consumer.RecordedEvent;

/**
 * Class responsible for holding rows, their values and textual
 * representation.
 */
final class Table {
    private final List<Row> rows = new ArrayList<>();
    private final List<Field> fields = new ArrayList<>();

    boolean isEmpty() {
        return rows.isEmpty();
    }

    void addRows(List<Row> rows) {
        this.rows.addAll(rows);
    }

    List<Row> getRows() {
        return rows;
    }

    void addFields(List<Field> fields) {
        for (int index = 0; index <fields.size(); index++) {
            if (fields.get(index).index != index) {
                throw new InternalError("Field index not in sync. with array position");
            }
        }
        this.fields.addAll(fields);
    }

    List<Field> getFields() {
        return fields;
    }

    public void add(RecordedEvent event, List<Field> sourceFields) {
        Row row = new Row(fields.size());
        for (Field field : sourceFields) {
            row.putValue(field.index, field.valueGetter.apply(event));
        }
        rows.add(row);
    }
}
