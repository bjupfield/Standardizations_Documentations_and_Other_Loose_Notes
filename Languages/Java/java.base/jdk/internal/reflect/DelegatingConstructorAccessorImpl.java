/*
 * Copyright (c) 2001, 2021, Oracle and/or its affiliates. All rights reserved.
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

package jdk.internal.reflect;

import jdk.internal.vm.annotation.Stable;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/** Delegates its invocation to another ConstructorAccessorImpl and can
    change its delegate at run time. */

class DelegatingConstructorAccessorImpl extends ConstructorAccessorImpl {
    // initial non-null delegate
    private final ConstructorAccessorImpl initialDelegate;
    // alternative delegate: starts as null;
    // only single change from null -> non-null is guaranteed
    @Stable
    private ConstructorAccessorImpl altDelegate;

    DelegatingConstructorAccessorImpl(ConstructorAccessorImpl delegate) {
        initialDelegate = Objects.requireNonNull(delegate);
    }

    public Object newInstance(Object[] args)
      throws InstantiationException,
             IllegalArgumentException,
             InvocationTargetException
    {
        return delegate().newInstance(args);
    }

    private ConstructorAccessorImpl delegate() {
        var d = altDelegate;
        return  d != null ? d : initialDelegate;
    }

    void setDelegate(ConstructorAccessorImpl delegate) {
        altDelegate = Objects.requireNonNull(delegate);
    }
}
