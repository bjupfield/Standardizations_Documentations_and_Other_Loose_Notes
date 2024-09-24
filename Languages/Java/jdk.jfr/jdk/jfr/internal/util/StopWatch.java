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
package jdk.jfr.internal.util;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public final class StopWatch {
    private record Timing(String name, Instant start) {
    }

    private final List<Timing> timings = new ArrayList<>();

    public void beginQueryValidation() {
        beginTask("query-validation");
    }

    public void beginAggregation() {
        beginTask("aggregation");
    }

    public void beginFormatting() {
        beginTask("formatting");
    }

    public void beginTask(String name) {
        timings.add(new Timing(name, Instant.now()));
    }

    public void finish() {
        beginTask("end");
    }

    @Override
    public String toString() {
        StringJoiner sb = new StringJoiner(", ");
        for (int i = 0; i < timings.size() - 1; i++) {
            Timing current = timings.get(i);
            Timing next = timings.get(i + 1);
            Duration d = Duration.between(current.start(), next.start());
            sb.add(current.name() + "=" + ValueFormatter.formatDuration(d));
        }
        return sb.toString();
    }
}
