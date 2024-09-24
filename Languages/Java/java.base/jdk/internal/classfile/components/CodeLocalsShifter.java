/*
 * Copyright (c) 2022, 2023, Oracle and/or its affiliates. All rights reserved.
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
package jdk.internal.classfile.components;

import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import jdk.internal.classfile.AccessFlags;
import jdk.internal.classfile.CodeTransform;
import jdk.internal.classfile.TypeKind;
import jdk.internal.classfile.impl.CodeLocalsShifterImpl;

/**
 * {@link CodeLocalsShifter} is a {@link CodeTransform} shifting locals to
 * newly allocated positions to avoid conflicts during code injection.
 * Locals pointing to the receiver or to method arguments slots are never shifted.
 * All locals pointing beyond the method arguments are re-indexed in order of appearance.
 */
public sealed interface CodeLocalsShifter extends CodeTransform permits CodeLocalsShifterImpl {

    /**
     * Creates a new instance of {@link CodeLocalsShifter}
     * with fixed local slots calculated from provided method information.
     * @param methodFlags flags of the method to construct {@link CodeLocalsShifter} for
     * @param methodDescriptor descriptor of the method to construct {@link CodeLocalsShifter} for
     * @return new instance of {@link CodeLocalsShifter}
     */
    static CodeLocalsShifter of(AccessFlags methodFlags, MethodTypeDesc methodDescriptor) {
        int fixed = methodFlags.has(AccessFlag.STATIC) ? 0 : 1;
        for (var param : methodDescriptor.parameterList())
            fixed += TypeKind.from(param).slotSize();
        return new CodeLocalsShifterImpl(fixed);
    }
}
