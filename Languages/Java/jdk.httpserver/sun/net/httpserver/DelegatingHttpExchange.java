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

package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public abstract class DelegatingHttpExchange extends HttpExchange {

    private final HttpExchange exchange;

    public DelegatingHttpExchange(HttpExchange ex) {
        this.exchange = ex;
    }

    public abstract Headers getRequestHeaders();

    public abstract String getRequestMethod();

    public abstract URI getRequestURI();

    public Headers getResponseHeaders() {
        return exchange.getResponseHeaders();
    }

    public HttpContext getHttpContext() {
        return exchange.getHttpContext();
    }

    public void close() {
        exchange.close();
    }

    public InputStream getRequestBody() {
        return exchange.getRequestBody();
    }

    public int getResponseCode() {
        return exchange.getResponseCode();
    }

    public OutputStream getResponseBody() {
        return exchange.getResponseBody();
    }

    public void sendResponseHeaders(int rCode, long contentLen) throws IOException {
        exchange.sendResponseHeaders(rCode, contentLen);
    }

    public InetSocketAddress getRemoteAddress() {
        return exchange.getRemoteAddress();
    }

    public InetSocketAddress getLocalAddress() {
        return exchange.getLocalAddress();
    }

    public String getProtocol() {
        return exchange.getProtocol();
    }

    public Object getAttribute(String name) {
        return exchange.getAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        exchange.setAttribute(name, value);
    }

    public void setStreams(InputStream i, OutputStream o) {
        exchange.setStreams(i, o);
    }

    public HttpPrincipal getPrincipal() {
        return exchange.getPrincipal();
    }
}
