/*
 * Copyright (c) 2021, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.toolkit.taglets.snippet;

import java.util.Objects;

/*
 * 1. The hierarchy of attributes is modelled as
 *
 *     Attribute
 *     |
 *     +- Valueless
 *     |
 *     +- Valued
 *
 * not as
 *
 *     Attribute (Valueless)
 *     |
 *     +- Valued
 *
 * because in conjunction with query operations of `Attributes`, `Valued` and
 * `Valueless` should be more useful if neither is a subtype of the other.
 *
 * 2. `Attribute` is abstract because its sole purpose is to be a category.
 *
 * 3. This attribute abstraction is simpler than that of com.sun.source.doctree.AttributeTree.
 * There's no need to have recursive structure similar to that of allowed by AttributeTree.
 */
/**
 * A markup attribute.
 */
public abstract class Attribute {

    private final String name;

    private final int nameStartPosition;

    private Attribute(String name, int nameStartPosition) {
        this.name = Objects.requireNonNull(name);
        this.nameStartPosition = nameStartPosition;
    }

    String name() {
        return name;
    }

    int nameStartPosition() {
        return nameStartPosition;
    }

    /*
     * `Valued` can be later extended by classes such as DoublyQuoted,
     * SinglyQuoted or Unquoted to form a (sealed) hierarchy. In that case,
     * `Valued` should become abstract similarly to `Attribute`.
     */
    static final class Valued extends Attribute {

        private final String value;

        private final int valueStartPosition;

        Valued(String name, String value, int namePosition, int valueStartPosition) {
            super(name, namePosition);
            this.value = Objects.requireNonNull(value);
            this.valueStartPosition = valueStartPosition;
        }

        String value() {
            return value;
        }

        public int valueStartPosition() {
            return valueStartPosition;
        }
    }

    static final class Valueless extends Attribute {

        Valueless(String name, int nameStartPosition) {
            super(name, nameStartPosition);
        }
    }
}
