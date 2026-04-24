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

package sun.net.httpserver.simpleserver;

import java.io.PrintWriter;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Programmatic entry point to start "java -m jdk.httpserver".
 */
public class Main {

    /**
     * This constructor should never be called.
     */
    private Main() { throw new AssertionError(); }

    /**
     * The main entry point.
     *
     * <p> The command line arguments are parsed and the server is started. If
     * started successfully, the server will run on a new non-daemon thread,
     * and this method will return. Otherwise, if the server is not started
     * successfully, e.g. an error is encountered while parsing the arguments
     * or an I/O error occurs, the server is not started and this method invokes
     * System::exit with an appropriate exit code.
     *
     * <p> If the system property "sun.net.httpserver.maxReqTime" has not been
     * set by the user, it is set to a value of 5 seconds. This is to prevent
     * the server from hanging indefinitely, for example in the case of an HTTPS
     * request.
     *
     * @param args the command-line options
     * @throws NullPointerException if {@code args} is {@code null}, or if there
     *         are any {@code null} values in the {@code args} array
     */
    public static void main(String... args) {
        setMaxReqTime();
        JWebServer.setMaxConnectionsIfNotSet();

        int ec = SimpleFileServerImpl.start(new PrintWriter(System.out, true, UTF_8), "java", args);
        if (ec != 0) {
            System.exit(ec);
        }  // otherwise, the server has either been started successfully and
           // runs in another non-daemon thread, or -h or -version have been
           // passed and the main thread has exited normally.
    }

    public static final String MAXREQTIME_KEY = "sun.net.httpserver.maxReqTime";
    public static final String MAXREQTIME_VAL = "5";

    private static void setMaxReqTime() {
        if (System.getProperty(MAXREQTIME_KEY) == null) {
            System.setProperty(MAXREQTIME_KEY, MAXREQTIME_VAL);
        }
    }
}
