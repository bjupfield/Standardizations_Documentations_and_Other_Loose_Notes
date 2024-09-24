/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
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
package jdk.jpackage.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import static jdk.jpackage.internal.OverridableResource.createResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class WixLauncherAsService extends LauncherAsService {

    WixLauncherAsService(String name, Map<String, ? super Object> mainParams) {
        super(name, mainParams,
                createResource("service-install.wxi", mainParams).setCategory(
                        I18N.getString("resource.launcher-as-service-wix-file")));

        serviceConfigResource = createResource("service-config.wxi", mainParams).setCategory(
                I18N.getString("resource.launcher-as-service-wix-file"));

        addSubstitutionDataEntry("SERVICE_NAME", getName());

        setPublicName(getResource());
        setPublicName(serviceConfigResource);
    }

    WixLauncherAsService setLauncherInstallPath(String v) {
        return addSubstitutionDataEntry("APPLICATION_LAUNCHER", v);
    }

    WixLauncherAsService setLauncherInstallPathId(String v) {
        return addSubstitutionDataEntry("APPLICATION_LAUNCHER_ID", v);
    }

    void writeServiceConfig(XMLStreamWriter xml) throws XMLStreamException,
            IOException {
        writeResource(serviceConfigResource, xml);
    }

    void writeServiceInstall(XMLStreamWriter xml) throws XMLStreamException,
            IOException {
        writeResource(getResource(), xml);
    }

    private WixLauncherAsService addSubstitutionDataEntry(String name,
            String value) {
        getResource().addSubstitutionDataEntry(name, value);
        serviceConfigResource.addSubstitutionDataEntry(name, value);
        return this;
    }

    private OverridableResource setPublicName(OverridableResource r) {
        return r.setPublicName(getName() + "-" + r.getDefaultName());
    }

    private void writeResource(OverridableResource resource, XMLStreamWriter xml)
            throws XMLStreamException, IOException {
        var buffer = new ByteArrayOutputStream();
        resource.saveToStream(buffer);

        try {
            Document doc = IOUtils.initDocumentBuilder().parse(
                    new ByteArrayInputStream(buffer.toByteArray()));

            XPath xPath = XPathFactory.newInstance().newXPath();

            NodeList nodes = (NodeList) xPath.evaluate("/Include/*", doc,
                    XPathConstants.NODESET);

            List<Source> sources = new ArrayList<>();
            for (int i = 0; i != nodes.getLength(); i++) {
                Node n = nodes.item(i);
                sources.add(new DOMSource(n));
            }

            IOUtils.mergeXmls(xml, sources);

        } catch (SAXException ex) {
            throw new IOException(ex);
        } catch (XPathExpressionException ex) {
            // Should never happen
            throw new RuntimeException(ex);
        }
    }

    private final OverridableResource serviceConfigResource;
}
