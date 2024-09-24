/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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
package jdk.jfr.internal;

import jdk.jfr.internal.event.EventWriter;

// This class is not directly used but renamed to
// jdk.jfr.internal.event.EventWriterFactory and loaded dynamically
// when the first event class is bytecode instrumented.
// See JVMUpcalls and EventWriterKey::ensureEventWriterFactory()
public final class EventWriterFactoryRecipe {
    private static final long KEY = EventWriterKey.getKey();

    public static EventWriter getEventWriter(long key) {
        if (key == KEY) {
            EventWriter ew = JVM.getEventWriter();
            return ew != null ? ew : JVM.newEventWriter();
        }
        EventWriterKey.block();
        return null; // Can't reach here.
    }
}
