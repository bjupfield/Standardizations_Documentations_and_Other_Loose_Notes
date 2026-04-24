/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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
package jdk.jfr.internal.query;

import java.time.Instant;

import jdk.jfr.internal.util.Output;

/**
 * Holds information on how a query should be rendered.
 */
public final class Configuration {
    public static final int MAX_PREFERRED_WIDTH = 120;
    public static final int MIN_PREFERRED_WIDTH = 40;
    public static final int PREFERRED_WIDTH = 80;

    public enum Truncate {
        BEGINNING, END
    }

    /**
     * Where the rendered result should be printed.
     */
    public Output output;

    /**
     * The title of the table or form.
     * <p>
     * {@code null) means no title.
     */
    public String title;

    /**
     * Truncation mode if text overflows.
     * <p>
     * If truncate is not set, it will be determined by heuristics.
     */
    public Truncate truncate;

    /**
     * Height of table cells.
     * <p>
     * If cellHeight is not set, it will be determined by heuristics.
     */
    public int cellHeight;

    /**
     * Width of a table or form.
     * <p>
     * If width is not set, it will be determined by heuristics.
     */
    public int width;

    /**
     * If additional information should be printed.
     */
    public boolean verbose;

    /**
     * If symbolic names should be printed for table headers.
     */
    public boolean verboseHeaders;

    /**
     * If the title of the table or form should be printed.
     */
    public boolean verboseTitle;

    /**
     * The start time for the query.
     * <p>
     * {@code null) means no start time.
     */
    public Instant startTime;

    /**
     * The end time for the query.
     * <p>
     * {@code null) means no end time.
     */
    public Instant endTime;
}
