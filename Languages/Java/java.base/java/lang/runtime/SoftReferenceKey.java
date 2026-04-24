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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Objects;

/**
 * {@link SoftReference} wrapper key for entries in the backing map.
 *
 * @param <T> key type
 *
 * @since 21
 *
 * Warning: This class is part of PreviewFeature.Feature.STRING_TEMPLATES.
 *          Do not rely on its availability.
 */
final class SoftReferenceKey<T> extends SoftReference<T> implements ReferenceKey<T> {
    /**
     * Saved hashcode of the key. Used when {@link SoftReference} is
     * null.
     */
    private final int hashcode;

    /**
     * Package-Protected constructor.
     *
     * @param key   unwrapped key value
     * @param queue reference queue
     */
    SoftReferenceKey(T key, ReferenceQueue<T> queue) {
        super(key, queue);
        this.hashcode = Objects.hashCode(key);
    }

    /**
     * Cleanup unused key. No need to enqueue since the key did not make it
     * into the map.
     */
    @Override
    public void unused() {
        clear();
    }

    @Override
    public boolean equals(Object obj) {
        // Necessary when removing a null reference
        if (obj == this) {
            return true;
        }
        // Necessary when comparing an unwrapped key
        if (obj instanceof ReferenceKey<?> key) {
            obj = key.get();
        }
        return Objects.equals(get(), obj);
    }

    @Override
    public int hashCode() {
        // Use saved hashcode
        return hashcode;
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName() + "#" + System.identityHashCode(this);
    }
}
