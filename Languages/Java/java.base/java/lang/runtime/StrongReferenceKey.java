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

package java.lang.runtime;

import java.util.Objects;

/**
 * Wrapper for querying the backing map. Avoids the overhead of an
 * {@link java.lang.ref.Reference} object.
 *
 * @param <T> key type
 *
 * @since 21
 *
 * Warning: This class is part of PreviewFeature.Feature.STRING_TEMPLATES.
 *          Do not rely on its availability.
 */
final class StrongReferenceKey<T> implements ReferenceKey<T> {
    T key;

    /**
     * Package-Protected constructor.
     *
     * @param key unwrapped key value
     */
    StrongReferenceKey(T key) {
        this.key = key;
    }

    /**
     * {@return the unwrapped key}
     */
    @Override
    public T get() {
        return key;
    }

    @Override
    public void unused() {
        key = null;
    }

    @Override
    public boolean equals(Object obj) {
        // Necessary when comparing an unwrapped key
        if (obj instanceof ReferenceKey<?> key) {
            obj = key.get();
        }
        return Objects.equals(get(), obj);
    }

    @Override
    public int hashCode() {
        // Use unwrapped key hash code
        return get().hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName() + "#" + System.identityHashCode(this);
    }
}
