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

/**
 * Enum describing the different ways values can be aggregated.
 */
enum Aggregator {
    /**
     * Dummy to indicate no aggregation is being used.
     */
    MISSING(" "),
    /**
     * Calculate the average value of all finite numeric values.
     */
    AVERAGE("AVG"),
    /**
     * Calculate the number of elements, including {@code null}.
     */
    COUNT("COUNT"),
    /**
     * Calculate the difference between the last and first finite numeric value.
     */
    DIFFERENCE("DIFF"),
    /**
     * The first value, including {@code null}.
     */
    FIRST("FIRST"),
    /**
     * The last value, including {@code null}.
     */
    LAST("LAST"),
    /**
     * Aggregate values into a comma-separated list, including {@code null}.
     */
    LIST("LIST"),
    /**
     * The highest numeric value.
     */
    MAXIMUM("MAX"),
    /**
     * The median of all finite numeric values.
     */
    MEDIAN("MEDIAN"),
    /**
     * The lowest numeric value.
     */
    MINIMUM("MIN"),
    /**
     * Calculate the 90th percentile of all finite numeric values.
     */
    P90("P90"),
    /**
     * Calculate the 95th percentile of all finite numeric values.
     */
    P95("P95"),
    /**
     * Calculate the 99th percentile of all finite numeric values.
     */
    P99("P99"),
    /**
     * Calculate the 99.9th percentile of all finite numeric values.
     */
    P999("P999"),
    /**
     * Calculate the standard deviation of all finite numeric values.
     */
    STANDARD_DEVIATION("STDEV"),
    /**
     * Calculate the sum of all finite numeric values.
     */
    SUM("SUM"),
    /**
     * Calculates the number of distinct values determined by invoking Object.equals.
     */
    UNIQUE("UNIQUE"),
    /**
     * The last elements, for an event type, that all share the same end timestamp.
     */
    LAST_BATCH("LAST_BATCH");

    public final String name;

    private Aggregator(String name) {
        this.name = name;
    }
}