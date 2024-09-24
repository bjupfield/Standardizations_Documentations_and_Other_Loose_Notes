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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jdk.jfr.EventType;
import jdk.jfr.consumer.EventStream;
import jdk.jfr.consumer.MetadataEvent;

final class QueryExecutor {
    private final List<QueryRun> queryRuns = new ArrayList<>();
    private final List<EventType> eventTypes = new ArrayList<>();
    private final EventStream stream;

    public QueryExecutor(EventStream stream) {
        this(stream, List.of());
    }

    public QueryExecutor(EventStream stream, Query query) {
        this(stream, List.of(query));
    }

    public QueryExecutor(EventStream stream, List<Query> queries) {
        this.stream = stream;
        for (Query query : queries) {
            queryRuns.add(new QueryRun(stream, query));
        }
        stream.setReuse(false);
        stream.setOrdered(true);
        stream.onMetadata(this::onMetadata);
    }

    public List<QueryRun> run() {
        stream.start();
        for (QueryRun run : queryRuns) {
            run.complete();
        }
        return queryRuns;
    }

    private void onMetadata(MetadataEvent e) {
        if (eventTypes.isEmpty()) {
            eventTypes.addAll(e.getEventTypes());
        }
        if (queryRuns.isEmpty()) {
            addQueryRuns();
        }
        for (QueryRun run : queryRuns) {
            run.onMetadata(e);
        }
    }

    private void addQueryRuns() {
        for (EventType type : eventTypes) {
            try {
                Query query = new Query("SELECT * FROM " + type.getName());
                QueryRun run = new QueryRun(stream, query);
                queryRuns.add(run);
            } catch (ParseException pe) {
                // The event name contained whitespace or similar, ignore.
            }
        }
    }

    public List<EventType> getEventTypes() {
        return eventTypes;
    }
}
