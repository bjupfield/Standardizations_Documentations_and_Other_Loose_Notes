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

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.NotificationHandler;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpSocketOption;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

/**
 * Unimplemented.
 */
public class SctpChannelImpl
        extends SctpChannel {

    public SctpChannelImpl(SelectorProvider provider) {
        super(provider);
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public Association association() {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public SctpChannel bind(SocketAddress local) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public SctpChannel bindAddress(InetAddress address) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public SctpChannel unbindAddress(InetAddress address) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public boolean connect(SocketAddress remote) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public boolean connect(SocketAddress remote,
                           int maxOutStreams,
                           int maxInStreams) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public boolean isConnectionPending() {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public boolean finishConnect() throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public Set<SocketAddress> getAllLocalAddresses() throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public Set<SocketAddress> getRemoteAddresses() throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public SctpChannel shutdown() throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public <T> T getOption(SctpSocketOption<T> name) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public <T> SctpChannel setOption(SctpSocketOption<T> name,
                                     T value) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public Set<SctpSocketOption<?>> supportedOptions() {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public <T> MessageInfo receive(ByteBuffer dst,
                                   T attachment,
                                   NotificationHandler<T> handler) throws IOException {
        throw UnsupportedUtil.sctpUnsupported();
    }

    @Override
    public int send(ByteBuffer src,
                    MessageInfo messageInfo) throws IOException {
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
