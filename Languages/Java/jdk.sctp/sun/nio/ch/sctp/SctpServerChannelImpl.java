/*
 * Copyright (c) 2009, 2022, Oracle and/or its affiliates. All rights reserved.
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
package sun.nio.ch.sctp;

import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import com.sun.nio.sctp.SctpSocketOption;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

/**
 * Unimplemented.
 */
public class SctpServerChannelImpl
        extends SctpServerChannel {

    public SctpServerChannelImpl(SelectorProvider provider) {
        super(provider);
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public SctpChannel accept() throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public SctpServerChannel bind(SocketAddress local,
                                  int backlog) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public SctpServerChannel bindAddress(InetAddress address) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public SctpServerChannel unbindAddress(InetAddress address) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public Set<SocketAddress> getAllLocalAddresses() throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public <T> T getOption(SctpSocketOption<T> name) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public <T> SctpServerChannel setOption(SctpSocketOption<T> name,
                                           T value) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public Set<SctpSocketOption<?>> supportedOptions() {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    protected void implConfigureBlocking(boolean block) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public void implCloseSelectableChannel() throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }
}
