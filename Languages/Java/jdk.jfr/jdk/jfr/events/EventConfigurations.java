/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
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

package jdk.jfr.events;
import jdk.jfr.internal.Utils;
import jdk.jfr.internal.event.EventConfiguration;

public final class EventConfigurations {
    public static final EventConfiguration SOCKET_READ = Utils.getConfiguration(SocketReadEvent.class);
    public static final EventConfiguration SOCKET_WRITE = Utils.getConfiguration(SocketWriteEvent.class);
    public static final EventConfiguration FILE_READ = Utils.getConfiguration(FileReadEvent.class);
    public static final EventConfiguration FILE_WRITE = Utils.getConfiguration(FileWriteEvent.class);
    public static final EventConfiguration FILE_FORCE = Utils.getConfiguration(FileForceEvent.class);
    public static final EventConfiguration ERROR_THROWN = Utils.getConfiguration(ErrorThrownEvent.class);
    public static final EventConfiguration EXCEPTION_THROWN = Utils.getConfiguration(ExceptionThrownEvent.class);
}
