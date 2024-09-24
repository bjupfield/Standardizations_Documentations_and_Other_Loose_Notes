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
package jdk.internal.classfile.impl;

import jdk.internal.classfile.CodeBuilder;
import jdk.internal.classfile.CodeElement;
import jdk.internal.classfile.Signature;
import jdk.internal.classfile.TypeKind;
import jdk.internal.classfile.components.CodeLocalsShifter;
import jdk.internal.classfile.instruction.IncrementInstruction;
import jdk.internal.classfile.instruction.LoadInstruction;
import jdk.internal.classfile.instruction.LocalVariable;
import jdk.internal.classfile.instruction.LocalVariableType;
import jdk.internal.classfile.instruction.StoreInstruction;

import java.util.Arrays;

public final class CodeLocalsShifterImpl implements CodeLocalsShifter {

    private int[] locals = new int[0];
    private final int fixed;

    public CodeLocalsShifterImpl(int fixed) {
        this.fixed = fixed;
    }

    @Override
    public void accept(CodeBuilder cob, CodeElement coe) {
        switch (coe) {
            case LoadInstruction li ->
                cob.loadInstruction(
                        li.typeKind(),
                        shift(cob, li.slot(), li.typeKind()));
            case StoreInstruction si ->
                cob.storeInstruction(
                        si.typeKind(),
                        shift(cob, si.slot(), si.typeKind()));
            case IncrementInstruction ii ->
                cob.incrementInstruction(
                        shift(cob, ii.slot(), TypeKind.IntType),
                        ii.constant());
            case LocalVariable lv ->
                cob.localVariable(
                        shift(cob, lv.slot(), TypeKind.fromDescriptor(lv.type().stringValue())),
                        lv.name(),
                        lv.type(),
                        lv.startScope(),
                        lv.endScope());
            case LocalVariableType lvt ->
                cob.localVariableType(
                        shift(cob, lvt.slot(),
                                (lvt.signatureSymbol() instanceof Signature.BaseTypeSig bsig)
                                        ? TypeKind.fromDescriptor(bsig.signatureString())
                                        : TypeKind.ReferenceType),
                        lvt.name(),
                        lvt.signature(),
                        lvt.startScope(),
                        lvt.endScope());
            default -> cob.with(coe);
        }
    }

    private int shift(CodeBuilder cob, int slot, TypeKind tk) {
        if (tk == TypeKind.VoidType)  throw new IllegalArgumentException("Illegal local void type");
        if (slot >= fixed) {
            int key = 2*slot - fixed + tk.slotSize() - 1;
            if (key >= locals.length) locals = Arrays.copyOf(locals, key + 20);
            slot = locals[key] - 1;
            if (slot < 0) {
                slot = cob.allocateLocal(tk);
                locals[key] = slot + 1;
                if (tk.slotSize() == 2) locals[key - 1] = slot + 1;
            }
        }
        return slot;
    }
}
