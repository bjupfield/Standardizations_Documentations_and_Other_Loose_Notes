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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import jdk.jfr.internal.query.Query.OrderElement;
import jdk.jfr.internal.query.Query.SortOrder;
/**
 * Class responsible for sorting a table according to an ORDER BY statement or
 * a heuristics.
 */
final class TableSorter {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static class ColumnComparator implements Comparator<Row> {
        private final int factor;
        private final int index;
        private final boolean lexical;

        public ColumnComparator(Field field, SortOrder order) {
            this.factor = sortOrderToFactor(determineSortOrder(field, order));
            this.index = field.index;
            this.lexical = field.lexicalSort;
        }

        private SortOrder determineSortOrder(Field field, SortOrder order) {
            if (order != SortOrder.NONE) {
                return order;
            }
            if (field.timespan || field.percentage) {
                return SortOrder.DESCENDING;
            }
            return SortOrder.ASCENDING;
        }

        int sortOrderToFactor(SortOrder order) {
            return order == SortOrder.DESCENDING ? -1 : 1;
        }

        @Override
        public int compare(Row rowA, Row rowB) {
            if (lexical) {
                return compareObjects(rowA.getText(index), rowB.getText(index));
            } else {
                return compareObjects(rowA.getValue(index), rowB.getValue(index));
            }
        }

        private int compareObjects(Object a, Object b) {
            if (a instanceof Comparable c1 && b instanceof Comparable c2) {
                return factor * c1.compareTo(c2);
            }
            return factor;
        }
    }

    private final Table table;
    private final Query query;

    public TableSorter(Table table, Query query) {
        this.table = table;
        this.query = query;
    }

    public void sort() {
        if (table.getFields().isEmpty()) {
            return;
        }
        if (query.orderBy.isEmpty()) {
            sortDefault();
            return;
        }
        sortOrderBy();
    }

    private void sortDefault() {
        if (sortAggregators()) {
            return;
        }
        if (sortGroupBy()) {
            return;
        }
        sortLeftMost();
    }

    private boolean sortAggregators() {
        return sortPredicate(field -> field.aggregator != Aggregator.MISSING);
    }

    private boolean sortGroupBy() {
        return sortPredicate(field -> query.groupBy.contains(field.grouper));
    }

    private void sortOrderBy() {
        for (OrderElement orderer : query.orderBy.reversed()) {
            sortPredicate(field -> field.orderer == orderer);
        }
    }

    private boolean sortPredicate(Predicate<Field> predicate) {
        boolean sorted = false;
        for (Field field : table.getFields()) {
            if (predicate.test(field)) {
                sort(field, determineSortOrder(field));
                sorted = true;
            }
        }
        return sorted;
    }

    private SortOrder determineSortOrder(Field field) {
        if (field.orderer == null) {
            return SortOrder.NONE;
        } else {
            return field.orderer.order();
        }
    }

    private void sortLeftMost() {
        sort(table.getFields().getFirst(), SortOrder.NONE);
    }

    private void sort(Field field, SortOrder order) {
        table.getRows().sort(new ColumnComparator(field, order));
    }
}
