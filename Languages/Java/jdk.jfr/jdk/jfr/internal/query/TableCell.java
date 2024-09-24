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

import java.util.ArrayList;
import java.util.List;
import jdk.jfr.internal.query.Configuration.Truncate;

final class TableCell {
    static final String ELLIPSIS = "...";
    static final String COLUMN_SEPARATOR = " ";
    static final int MINIMAL_CELL_WIDTH = 1 + COLUMN_SEPARATOR.length();
    static final int ELLIPSIS_LENGTH = ELLIPSIS.length();

    private final List<String> lines = new ArrayList<>();
    private final Truncate truncate;
    private int preferredWidth;

    final int cellHeight;
    final Field field;
    int width;

    public TableCell(Field field, int cellHeight, Truncate truncate) {
        this.field = field;
        this.cellHeight = cellHeight;
        this.truncate = truncate;
    }

    public int getContentWidth() {
        return width - COLUMN_SEPARATOR.length();
    }

    public int getHeight() {
        return lines.size();
    }

    public String getText(int rowIndex) {
        return lines.get(rowIndex);
    }

    public void setPreferredWidth(int width) {
        preferredWidth = width;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }
    public void addLine(String text) {
        int contentWidth = getContentWidth();
        if (text.length() >= contentWidth) {
            add(truncate(text, contentWidth));
        } else {
            addAligned(text);
        }
    }

    public void setContent(String text) {
        clear();
        int contentSize = getContentSize();
        // Bail out early to prevent ellipsis when size is the same
        if (text.length() == contentSize) {
            add(text);
            return;
        }
        // Text is larger than size of the cell, truncate
        if (text.length() >= contentSize) {
            add(truncate(text, contentSize));
            return;
        }
        // Text fits on one line, pad left or right depending on alignment
        int contentWidth = getContentWidth();
        if (text.length() < contentWidth) {
            addAligned(text);
            return;
        }
        // Multiple lines and text fits cell
        add(text);
    }

    private void addAligned(String text) {
        String padding = " ".repeat(getContentWidth() - text.length());
        if (field.alignLeft) {
            add(text + padding);
        } else {
            add(padding + text);
        }
    }

    public int getContentSize() {
        return cellHeight * getContentWidth();
    }

    public List<String> getLines() {
        return lines;
    }

    private String truncate(String text, int size) {
        if (size < ELLIPSIS_LENGTH) {
            return ELLIPSIS.substring(0, ELLIPSIS_LENGTH - size);
        }
        int textSize = size - ELLIPSIS_LENGTH;
        if (truncate == Truncate.BEGINNING) {
            return ELLIPSIS + text.substring(text.length() - textSize);
        } else {
            return text.substring(0, textSize) + ELLIPSIS;
        }
    }

    private void add(String text) {
        int contentWidth = getContentWidth();
        int contentSize = getContentSize();
        for (int index = 0; index < contentSize; index += contentWidth) {
            int end = index + contentWidth;
            if (end >= text.length()) {
                String content = text.substring(index);
                content += " ".repeat(contentWidth - content.length());
                lines.add(content);
                return;
            }
            lines.add(text.substring(index, end));
        }
    }

    public void clear() {
        lines.clear();
    }
}