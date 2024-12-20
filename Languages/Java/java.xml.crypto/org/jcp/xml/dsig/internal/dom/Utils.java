/*
 * Copyright (c) 2007, 2024, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Copyright (c) 2005, 2023, Oracle and/or its affiliates. All rights reserved.
 */
package org.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.crypto.XMLCryptoContext;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Miscellaneous static utility methods for use in JSR 105 RI.
 *
 */
public final class Utils {

    private static final com.sun.org.slf4j.internal.Logger LOG =
        com.sun.org.slf4j.internal.LoggerFactory.getLogger(Utils.class);
    private static final String SECVAL_PROP_NAME =
        "org.jcp.xml.dsig.secureValidation";
    private static final boolean SECVAL_SYSPROP_SET;
    private static final boolean SECVAL_SYSPROP;
    static {
        String sysProp = privilegedGetProperty(SECVAL_PROP_NAME);
        SECVAL_SYSPROP_SET = sysProp != null;
        SECVAL_SYSPROP = Boolean.parseBoolean(sysProp);
        if (SECVAL_SYSPROP_SET && !SECVAL_SYSPROP) {
            LOG.warn("Secure validation mode disabled");
        }
    }

    private Utils() {}

    public static byte[] readBytesFromStream(InputStream is)
        throws IOException
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            while (true) {
                int read = is.read(buf);
                if (read == -1) { // EOF
                    break;
                }
                baos.write(buf, 0, read);
                if (read < 1024) {
                    break;
                }
            }
            return baos.toByteArray();
        }
    }

    /**
     * Converts an Iterator to a Set of Nodes, according to the XPath
     * Data Model.
     *
     * @param i the Iterator
     * @return the Set of Nodes
     */
    static Set<Node> toNodeSet(Iterator<?> i) {
        Set<Node> nodeSet = new HashSet<>();
        while (i.hasNext()) {
            Node n = (Node)i.next();
            nodeSet.add(n);
            // insert attributes nodes to comply with XPath
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap nnm = n.getAttributes();
                for (int j = 0, length = nnm.getLength(); j < length; j++) {
                    nodeSet.add(nnm.item(j));
                }
            }
        }
        return nodeSet;
    }

    /**
     * Returns the ID from a same-document URI (ex: "#id")
     */
    public static String parseIdFromSameDocumentURI(String uri) {
        if (uri.length() == 0) {
            return null;
        }
        String id = uri.substring(1);
        if (id.startsWith("xpointer(id(")) {
            int i1 = id.indexOf('\'');
            int i2 = id.indexOf('\'', i1+1);
            if (i1 >= 0 && i2 >= 0) {
                id = id.substring(i1 + 1, i2);
            }
        }
        return id;
    }

    /**
     * Returns true if uri is a same-document URI, false otherwise.
     */
    public static boolean sameDocumentURI(String uri) {
        return uri != null && (uri.length() == 0 || uri.charAt(0) == '#');
    }

    @SuppressWarnings("removal")
    private static String privilegedGetProperty(String theProp) {
        if (System.getSecurityManager() == null) {
            return System.getProperty(theProp);
        } else {
            return AccessController.doPrivileged(
                 (PrivilegedAction<String>) () -> System.getProperty(theProp));
        }
    }

    static boolean secureValidation(XMLCryptoContext xc) {
        // If set, system property supersedes XMLCryptoContext property
        if (SECVAL_SYSPROP_SET) {
            return SECVAL_SYSPROP;
        }
        if (xc == null) {
            return false;
        }
        return getBoolean(xc, SECVAL_PROP_NAME);
    }

    private static boolean getBoolean(XMLCryptoContext xc, String name) {
        Boolean value = (Boolean) xc.getProperty(name);
        return value != null && value;
    }
}
