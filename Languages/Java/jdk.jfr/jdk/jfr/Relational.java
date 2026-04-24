/*
 * Copyright (c) 2016, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.jfr;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta annotation for relational annotations, to be used on an annotation.
 * <p>
 * The following example shows how a relational annotation can be created and
 * used. The {@code Orderid} annotation indicates there is a relation between
 * {@code OrderEvent} and {@code OrderLineEvent}. if they have the same ID,
 * the order line belongs to the order.
 *
 * {@snippet class="Snippets" region="RelationalOverview"}
 *
 * @since 9
 */
@MetadataDefinition
@Label("Relation")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Relational {
}
