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
import java.util.Objects;

import jdk.jfr.EventType;
import jdk.jfr.Experimental;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.internal.Utils;

/**
 * Type referenced in a FROM-clause.
 * <p>
 * If the query has a WHEN clause, the available events for the event type
 * is restricted by a list of filter conditions.
 */
final class FilteredType {
    public record Filter (Field field, String value) {

        @Override
        public int hashCode() {
            return field.name.hashCode() + value.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Filter that) {
                return this.field.name.equals(that.field.name) && Objects.equals(this.value, that.value);
            }
            return false;
        }
    }

    private final List<Filter> filters = new ArrayList<>();
    private final EventType eventType;
    private final String simpleName;

    public FilteredType(EventType type) {
        this.eventType = type;
        this.simpleName = Utils.makeSimpleName(type);
    }

    public boolean isExperimental() {
        return eventType.getAnnotation(Experimental.class) != null;
    }

    public String getName() {
        return eventType.getName();
    }

    public String getLabel() {
        return eventType.getLabel();
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public ValueDescriptor getField(String name) {
        return eventType.getField(name);
    }

    public List<ValueDescriptor> getFields() {
        return eventType.getFields();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(eventType.getId()) + filters.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof FilteredType that) {
            return that.eventType.getId() == this.eventType.getId()
                && that.filters.equals(this.filters);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(" ");
        for (Filter condition : filters) {
            sb.append(condition.field());
            sb.append(" = ");
            sb.append(condition.value());
            sb.append(" ");
        }
        return sb.toString();
    }
}
