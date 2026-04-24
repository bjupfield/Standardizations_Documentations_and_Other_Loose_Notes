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
 */
package jdk.vm.ci.hotspot;

import java.util.Map;

import jdk.internal.vm.VMSupport.AnnotationDecoder;
import jdk.vm.ci.meta.AnnotationData;
import jdk.vm.ci.meta.EnumData;
import jdk.vm.ci.meta.ErrorData;
import jdk.vm.ci.meta.JavaType;
import jdk.vm.ci.meta.MetaUtil;
import jdk.vm.ci.meta.ResolvedJavaType;
import jdk.vm.ci.meta.UnresolvedJavaType;

/**
 * Implementation of {@link AnnotationDecoder} that resolves type names to {@link JavaType} values
 * and employs {@link AnnotationData} and {@link EnumData} to represent decoded annotations and enum
 * constants respectively.
 */
final class AnnotationDataDecoder implements AnnotationDecoder<JavaType, AnnotationData, EnumData, ErrorData> {

    static final AnnotationDataDecoder INSTANCE = new AnnotationDataDecoder();

    @Override
    public JavaType resolveType(String name) {
        String internalName = MetaUtil.toInternalName(name);
        return UnresolvedJavaType.create(internalName);
    }

    @Override
    public AnnotationData newAnnotation(JavaType type, Map.Entry<String, Object>[] elements) {
        return new AnnotationData(type, elements);
    }

    @Override
    public EnumData newEnumValue(JavaType enumType, String name) {
        return new EnumData(enumType, name);
    }

    @Override
    public ErrorData newErrorValue(String description) {
        return new ErrorData(description);
    }

    static ResolvedJavaType[] asArray(ResolvedJavaType type1, ResolvedJavaType type2, ResolvedJavaType... types) {
        ResolvedJavaType[] filter = new ResolvedJavaType[2 + types.length];
        filter[0] = type1;
        filter[1] = type2;
        System.arraycopy(types, 0, filter, 2, types.length);
        return filter;
    }
}
