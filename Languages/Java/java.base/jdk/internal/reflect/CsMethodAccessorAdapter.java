/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
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

import jdk.internal.vm.annotation.ForceInline;
import jdk.internal.vm.annotation.Hidden;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * MethodAccessor adapter for caller-sensitive methods which have
 * an alternate non-CSM method with the same method name but an additional
 * caller class argument.
 *
 * When a caller-sensitive method is called,
 * Method::invoke(Object target, Object[] args, Class<?> caller) will
 * be invoked with the caller class.  If an adapter is present,
 * the adapter method with the caller class parameter will be called
 * instead.
 */
class CsMethodAccessorAdapter extends MethodAccessorImpl {
    private final Method csmAdapter;
    private final MethodAccessor accessor;

    CsMethodAccessorAdapter(Method method, Method csmAdapter, MethodAccessor accessor) {
        assert Reflection.isCallerSensitive(method) && !Reflection.isCallerSensitive(csmAdapter);
        this.csmAdapter = csmAdapter;
        this.accessor = accessor;
    }

    @Override
    public Object invoke(Object obj, Object[] args)
            throws IllegalArgumentException, InvocationTargetException {
        throw new InternalError("caller-sensitive method invoked without explicit caller: " + csmAdapter);
    }

    @Override
    @ForceInline
    @Hidden
    public Object invoke(Object obj, Object[] args, Class<?> caller)
            throws IllegalArgumentException, InvocationTargetException {
        Object[] newArgs = new Object[args == null ? 1 : args.length + 1];
        newArgs[0] = caller;
        if (args != null) {
            System.arraycopy(args, 0, newArgs, 1, args.length);
        }
        return accessor.invoke(obj, newArgs);
    }
}
