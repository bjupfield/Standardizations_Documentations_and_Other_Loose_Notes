/*
 * Copyright (c) 2017, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.formats.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import jdk.javadoc.internal.doclets.formats.html.markup.ContentBuilder;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlAttr;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlTree;
import jdk.javadoc.internal.doclets.toolkit.Content;

/**
 * A row of header cells for an HTML table.
 *
 * The header contains a list of {@code <th>} cells, providing the column headers.
 * The attribute {@code scope="col"} is automatically added to each header cell.
 * In addition, a series of style class names can be specified, to be applied one per cell.
 */
public class TableHeader extends Content {

    /**
     * The content to be put in each of the {@code <th>} cells in the header row.
     */
    private final List<Content> cellContents;
    /**
     * The style class names for each of the {@code <th>} cells in the header row.
     * If not set, default style names will be used.
     */
    private List<HtmlStyle> styles;

    private boolean[] sortable;

    /**
     * Creates a header row, with localized content for each cell.
     * Resources keys will be converted to content using {@link Contents#getContent(String)}.
     * @param contents a factory to get the content for each header cell.
     * @param colHeaderKeys the resource keys for the content in each cell.
     */
    public TableHeader(Contents contents, String... colHeaderKeys) {
        this.cellContents = Arrays.stream(colHeaderKeys)
                .map(contents::getContent)
                .toList();
    }

    /**
     * Creates a header row, with specified content for each cell.
     * @param headerCellContents a content object for each header cell
     */
    public TableHeader(Content... headerCellContents) {
        this.cellContents = Arrays.asList(headerCellContents);
    }

    /**
     * Creates a header row, with specified content for each cell.
     * @param headerCellContents a content object for each header cell
     */
    public TableHeader(List<Content> headerCellContents) {
        this.cellContents = headerCellContents;
    }

    /**
     * Set the style class names for each header cell.
     * The number of names must match the number of cells given to the constructor.
     * @param styles the style class names
     * @return this object
     */
    public TableHeader styles(HtmlStyle... styles) {
        if (styles.length != cellContents.size()) {
            throw new IllegalStateException();
        }
        this.styles = Arrays.asList(styles);
        return this;
    }

    /**
     * Makes the table sortable by the content of columns for which the
     * argument boolean array contains {@code true}.
     * @param sortable boolean array specifying sortable columns
     * @return this object
     */
    public TableHeader sortable(boolean... sortable) {
        if (sortable.length != cellContents.size()) {
            throw new IllegalStateException();
        }
        this.sortable = sortable;
        return this;
    }

    /**
     * Set the style class names for each header cell.
     * The number of names must match the number of cells given to the constructor.
     * @param styles the style class names
     * @return this object
     */
    public TableHeader styles(List<HtmlStyle> styles) {
        if (styles.size() != cellContents.size()) {
            throw new IllegalStateException();
        }
        this.styles = styles;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec This implementation always returns {@code false}.
     *
     * @return {@code false}
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean write(Writer out, String newline, boolean atNewline) throws IOException {
        return toContent().write(out, newline, atNewline);
    }

    /**
     * Converts this header to a {@link Content} object, for use in an {@link HtmlTree}.
     * @return a Content object
     */
    private Content toContent() {
        Content header = new ContentBuilder();
        int i = 0;
        for (Content cellContent : cellContents) {
            HtmlStyle style = (styles != null) ? styles.get(i)
                    : (i == 0) ? HtmlStyle.colFirst
                    : (i == (cellContents.size() - 1)) ? HtmlStyle.colLast
                    : (i == 1) ? HtmlStyle.colSecond : null;
            var cell = HtmlTree.DIV(HtmlStyle.tableHeader, cellContent);
            if (style != null) {
                cell.addStyle(style);
            }
            if (sortable != null && sortable[i]) {
                cell.put(HtmlAttr.ONCLICK, "sortTable(this, " + i + ", " + sortable.length +")");
                // Current tables happen to be sorted by first column by default, this may not hold true for future uses.
                if (i == 0) {
                    cell.addStyle("sort-asc");
                }
            }
            header.add(cell);
            i++;
        }
        return header;
    }

}
