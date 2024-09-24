/*
 * Copyright (c) 2010, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.formats.html.markup;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.DocFile;
import jdk.javadoc.internal.doclets.toolkit.util.DocFileIOException;

/**
 * Class for generating an HTML document for javadoc output.
 */
public class HtmlDocument {
    private final DocType docType = DocType.HTML5;
    private final Content docContent;

    /**
     * Constructs an HTML document.
     *
     * @param html the {@link TagName#HTML HTML} element of the document
     */
    public HtmlDocument(Content html) {
        docContent = html;
    }

    /**
     * Writes the content of this document to the specified file.
     * Newlines are written using the platform line separator.
     *
     * @param docFile the file
     * @throws DocFileIOException if an {@code IOException} occurs while writing the file
     */
    public void write(DocFile docFile) throws DocFileIOException {
        try (Writer writer = docFile.openWriter()) {
            write(writer, DocFile.PLATFORM_LINE_SEPARATOR);
        } catch (IOException e) {
            throw new DocFileIOException(docFile, DocFileIOException.Mode.WRITE, e);
        }
    }

    @Override
    public String toString() {
        try (Writer writer = new StringWriter()) {
            write(writer, "\n");
            return writer.toString();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    private void write(Writer writer, String newline) throws IOException {
        writer.write(docType.text);
        writer.write(newline);
        docContent.write(writer, newline, true);
    }
}
