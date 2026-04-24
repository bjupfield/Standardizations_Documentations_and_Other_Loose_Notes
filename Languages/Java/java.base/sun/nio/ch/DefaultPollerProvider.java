/*
 * Copyright (c) 2019, 2022, Oracle and/or its affiliates. All rights reserved.
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
package sun.nio.ch;

import java.io.IOException;

/**
 * Default PollerProvider for Windows based on wepoll.
 */
class DefaultPollerProvider extends PollerProvider {
    DefaultPollerProvider() { }

    @Override
    Poller readPoller() throws IOException {
        return new WEPollPoller(true);
    }

    @Override
    Poller writePoller() throws IOException {
        return new WEPollPoller(false);
    }
}
