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
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * View/wrapper of keys used by the backing {@link ReferencedKeyMap}.
 * There are two style of keys; one for entries in the backing map and
 * one for queries to the backing map. This second style avoids the
 * overhead of a {@link Reference} object.
 *
 * @param <T> key type
 *
 * @since 21
 *
 * Warning: This class is part of PreviewFeature.Feature.STRING_TEMPLATES.
 *          Do not rely on its availability.
 */
sealed interface ReferenceKey<T> permits StrongReferenceKey, WeakReferenceKey, SoftReferenceKey {
    /**
     * {@return the value of the unwrapped key}
     */
    T get();

    /**
     * Cleanup unused key.
     */
    void unused();

}
