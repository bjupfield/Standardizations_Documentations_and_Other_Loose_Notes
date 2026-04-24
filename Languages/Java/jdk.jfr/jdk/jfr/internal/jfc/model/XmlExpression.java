/*
 * Copyright (c) 2021, 2022, Oracle and/or its affiliates. All rights reserved.
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
package jdk.jfr.internal.jfc.model;

import java.util.List;

// Base class for <condition>, <or>, <not>, <and> and <test>
abstract class XmlExpression extends XmlElement {

    public final List<XmlExpression> getExpressions() {
        return elements(XmlExpression.class);
    }

    @Override
    protected List<Constraint> constraints() {
        return List.of(
            Constraint.any(XmlOr.class),
            Constraint.any(XmlAnd.class),
            Constraint.any(XmlTest.class),
            Constraint.any(XmlNot.class)
        );
    }

    @Override
    protected void validateChildConstraints() throws JFCModelException {
        if (getExpressions().size() < 2) {
            throw new JFCModelException("Expected + <" + getElementName() + "> to have at least two children");
        }
    }
}
