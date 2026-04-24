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

package jdk.internal.foreign;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.ref.Cleaner;

import jdk.internal.vm.annotation.ForceInline;

/**
 * A confined session, which features an owner thread. The liveness check features an additional
 * confinement check - that is, calling any operation on this session from a thread other than the
 * owner thread will result in an exception. Because of this restriction, checking the liveness bit
 * can be performed in plain mode.
 */
final class ConfinedSession extends MemorySessionImpl {

    private int asyncReleaseCount = 0;

    static final VarHandle ASYNC_RELEASE_COUNT;

    static {
        try {
            ASYNC_RELEASE_COUNT = MethodHandles.lookup().findVarHandle(ConfinedSession.class, "asyncReleaseCount", int.class);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public ConfinedSession(Thread owner) {
        super(owner, new ConfinedResourceList());
    }

    @Override
    @ForceInline
    public void acquire0() {
        checkValidState();
        if (state == MAX_FORKS) {
            throw tooManyAcquires();
        }
        state++;
    }

    @Override
    @ForceInline
    public void release0() {
        if (Thread.currentThread() == owner) {
            state--;
        } else {
            // It is possible to end up here in two cases: this session was kept alive by some other confined session
            // which is implicitly released (in which case the release call comes from the cleaner thread). Or,
            // this session might be kept alive by a shared session, which means the release call can come from any
            // thread.
            ASYNC_RELEASE_COUNT.getAndAdd(this, 1);
        }
    }

    void justClose() {
        checkValidState();
        int asyncCount = (int)ASYNC_RELEASE_COUNT.getVolatile(this);
        if ((state == 0 && asyncCount == 0)
                || ((state - asyncCount) == 0)) {
            state = CLOSED;
        } else {
            throw alreadyAcquired(state - asyncCount);
        }
    }

    /**
     * A confined resource list; no races are possible here.
     */
    static final class ConfinedResourceList extends ResourceList {
        @Override
        void add(ResourceCleanup cleanup) {
            if (fst != ResourceCleanup.CLOSED_LIST) {
                cleanup.next = fst;
                fst = cleanup;
            } else {
                throw alreadyClosed();
            }
        }

        @Override
        void cleanup() {
            if (fst != ResourceCleanup.CLOSED_LIST) {
                ResourceCleanup prev = fst;
                fst = ResourceCleanup.CLOSED_LIST;
                cleanup(prev);
            } else {
                throw alreadyClosed();
            }
        }
    }
}
