/*
 * Copyright (c) 1998, 2022, Oracle and/or its affiliates. All rights reserved.
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

/*
 * This source code is provided to illustrate the usage of a given feature
 * or technique and has been deliberately simplified. Additional steps
 * required for a production-quality application, such as security checks,
 * input validation and proper error handling, might not be present in
 * this sample code.
 */


package com.sun.tools.example.debug.tty;

import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;

class ThreadIterator implements Iterator<ThreadReference> {
    Iterator<ThreadReference> it = null;
    ThreadGroupIterator tgi;
    Iterator<ThreadInfo> vthreadIter;

    ThreadIterator(ThreadGroupReference tg) {
        tgi = new ThreadGroupIterator(tg);
        if (tg == Env.vm().topLevelThreadGroups().get(0)) {
            // This means all groups are included, so include vthreads.
            vthreadIter = ThreadInfo.vthreads().iterator();
        }
    }

    ThreadIterator() {
        tgi = new ThreadGroupIterator();
        vthreadIter = ThreadInfo.vthreads().iterator();
    }

    @Override
    public boolean hasNext() {
        while (it == null || !it.hasNext()) {
            if (!tgi.hasNext()) {
                return (vthreadIter == null ? false : vthreadIter.hasNext());
            } else {
                it = tgi.nextThreadGroup().threads().iterator();
            }
        }
        return true;
    }

    @Override
    public ThreadReference next() {
        if (it.hasNext()) {
            return it.next();
        } else {
            if (vthreadIter == null) {
                throw new NoSuchElementException();
            } else {
                return vthreadIter.next().getThread();
            }
        }
    }

    public ThreadReference nextThread() {
        return next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
