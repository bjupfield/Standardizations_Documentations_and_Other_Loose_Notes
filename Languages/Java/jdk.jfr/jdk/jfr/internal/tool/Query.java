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

package jdk.jfr.internal.tool;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import jdk.jfr.consumer.EventStream;
import jdk.jfr.internal.util.UserDataException;
import jdk.jfr.internal.util.Output.BufferedPrinter;
import jdk.jfr.internal.util.UserSyntaxException;
import jdk.jfr.internal.query.QueryPrinter;
import jdk.jfr.internal.query.Configuration.Truncate;
import jdk.jfr.internal.query.Configuration;

final class Query extends Command {
    @Override
    public String getName() {
        return "query";
    }

    @Override
    public String getDescription() {
        return "Display event values in a recording file (.jfr) in a tabular format";
    }

    @Override
    public void displayOptionUsage(PrintStream p) {
                // 0123456789001234567890012345678900123456789001234567890012345678900123456789001234567890
        p.println("  --verbose               Displays the symbolic column names");
        p.println();
        p.println("  --width <integer>       The width of the table. Default value depends on the query");
        p.println();
        p.println("  <query>                 Query, for example \"SELECT * FROM GarbageCollection\"");
        p.println("                          See below for grammar.");
        p.println();
        p.println("  <file>                  Location of the recording file (.jfr)");
        p.println();
        p.println(QueryPrinter.getGrammarText());
        p.println();
        p.println("Example usage:");
        p.println();
        p.println(" $ jfr query \"SHOW EVENTS\" recording.jfr");
        p.println();
        p.println(" $ jfr query \"SHOW FIELDS ObjectAllocationSample\" recording.jfr");
        p.println();
        p.println(" $ jfr query --verbose \"SELECT * FROM ObjectAllocationSample\" recording.jfr");
        p.println();
        p.println(" $ jfr query --width 160 \"SELECT pid, path FROM SystemProcess\" recording.jfr");
        p.println();
        p.println(" $ jfr query \"SELECT stackTrace.topFrame AS T, SUM(weight)");
        p.println("              FROM ObjectAllocationSample GROUP BY T\" recording.jfr");
        p.println();
        p.println("$ jfr JFR.query \"COLUMN 'Method', 'Percentage'");
        p.println("                 FORMAT default, normalized;width:10");
        p.println("                 SELECT stackTrace.topFrame AS T, COUNT(*) AS C");
        p.println("                 GROUP BY T");
        p.println("                 FROM ExecutionSample ORDER BY C DESC\" recording.jfr");
        p.println();
        p.println("$ jcmd <pid> JFR.query \"COLUMN 'Start', 'GC ID', 'Heap Before GC',");
        p.println("                        'Heap After GC', 'Longest Pause'");
        p.println("                        SELECT G.startTime, G.gcId, B.heapUsed,");
        p.println("                             A.heapUsed, longestPause");
        p.println("                        FROM GarbageCollection AS G,");
        p.println("                             GCHeapSummary AS B,");
        p.println("                             GCHeapSummary AS A");
        p.println("                        WHERE B.when = 'Before GC' AND A.when = 'After GC'");
        p.println("                        GROUP BY gcId");
        p.println("                        ORDER BY G.startTime\" recording.jfr");
   }

    @Override
    public List<String> getOptionSyntax() {
        List<String> list = new ArrayList<>();
        list.add("[--verbose] [--width <integer>] <query> <file>");
        return list;
    }

    @Override
    public void execute(Deque<String> options) throws UserSyntaxException, UserDataException {
        Path file = getJFRInputFile(options);
        int optionCount = options.size();
        var configuration = new Configuration();
        BufferedPrinter printer = new BufferedPrinter(System.out);
        configuration.output = printer;
        while (optionCount > 0) {
            if (acceptSwitch(options, "--verbose")) {
                configuration.verbose = true;
                configuration.verboseHeaders = true;
            }
            if (acceptOption(options, "--truncate")) {
                String mode = options.remove();
                try {
                    configuration.truncate = Truncate.valueOf(mode.toUpperCase());
                } catch (IllegalArgumentException iae) {
                    throw new UserSyntaxException("truncate must be 'beginning' or 'end'");
                }
            }
            if (acceptOption(options, "--cell-height")) {
                configuration.cellHeight = acceptInt(options, "cell-height");
            }
            if (acceptOption(options, "--width")) {
                configuration.width = acceptInt(options, "width");
            }
            if (optionCount == 1) {
                String query = options.pop();
                try (EventStream stream = EventStream.openFile(file)) {
                    QueryPrinter qp = new QueryPrinter(configuration, stream);
                    qp.execute(query);
                    printer.flush();
                } catch (IOException ioe) {
                    couldNotReadError(file, ioe);
                }
                return;
            }
            if (optionCount == options.size()) {
                throw new UserSyntaxException("unknown option " + options.peek());
            }
            optionCount = options.size();
        }
    }
}
