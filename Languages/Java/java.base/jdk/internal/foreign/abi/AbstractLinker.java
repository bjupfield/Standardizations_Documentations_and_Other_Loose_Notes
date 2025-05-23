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
package jdk.internal.foreign.abi;

import jdk.internal.foreign.SystemLookup;
import jdk.internal.foreign.Utils;
import jdk.internal.foreign.abi.aarch64.linux.LinuxAArch64Linker;
import jdk.internal.foreign.abi.aarch64.macos.MacOsAArch64Linker;
import jdk.internal.foreign.abi.aarch64.windows.WindowsAArch64Linker;
import jdk.internal.foreign.abi.fallback.FallbackLinker;
import jdk.internal.foreign.abi.ppc64.linux.LinuxPPC64leLinker;
import jdk.internal.foreign.abi.riscv64.linux.LinuxRISCV64Linker;
import jdk.internal.foreign.abi.s390.linux.LinuxS390Linker;
import jdk.internal.foreign.abi.x64.sysv.SysVx64Linker;
import jdk.internal.foreign.abi.x64.windows.Windowsx64Linker;
import jdk.internal.foreign.layout.AbstractLayout;
import jdk.internal.reflect.CallerSensitive;
import jdk.internal.reflect.Reflection;

import java.lang.foreign.AddressLayout;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.PaddingLayout;
import java.lang.foreign.SequenceLayout;
import java.lang.foreign.StructLayout;
import java.lang.foreign.UnionLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.List;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.Set;

public abstract sealed class AbstractLinker implements Linker permits LinuxAArch64Linker, MacOsAArch64Linker,
                                                                      SysVx64Linker, WindowsAArch64Linker,
                                                                      Windowsx64Linker, LinuxPPC64leLinker,
                                                                      LinuxRISCV64Linker, LinuxS390Linker,
                                                                      FallbackLinker {

    public interface UpcallStubFactory {
        MemorySegment makeStub(MethodHandle target, Arena arena);
    }

    private record LinkRequest(FunctionDescriptor descriptor, LinkerOptions options) {}
    private final SoftReferenceCache<LinkRequest, MethodHandle> DOWNCALL_CACHE = new SoftReferenceCache<>();
    private final SoftReferenceCache<LinkRequest, UpcallStubFactory> UPCALL_CACHE = new SoftReferenceCache<>();

    @Override
    @CallerSensitive
    public final MethodHandle downcallHandle(MemorySegment symbol, FunctionDescriptor function, Option... options) {
        Reflection.ensureNativeAccess(Reflection.getCallerClass(), Linker.class, "downcallHandle");
        SharedUtils.checkSymbol(symbol);
        return downcallHandle0(function, options).bindTo(symbol);
    }

    @Override
    @CallerSensitive
    public final MethodHandle downcallHandle(FunctionDescriptor function, Option... options) {
        Reflection.ensureNativeAccess(Reflection.getCallerClass(), Linker.class, "downcallHandle");
        return downcallHandle0(function, options);
    }

    private final MethodHandle downcallHandle0(FunctionDescriptor function, Option... options) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(options);
        checkLayouts(function);
        function = stripNames(function);
        LinkerOptions optionSet = LinkerOptions.forDowncall(function, options);
        validateVariadicLayouts(function, optionSet);

        return DOWNCALL_CACHE.get(new LinkRequest(function, optionSet), linkRequest ->  {
            FunctionDescriptor fd = linkRequest.descriptor();
            MethodType type = fd.toMethodType();
            MethodHandle handle = arrangeDowncall(type, fd, linkRequest.options());
            handle = SharedUtils.maybeCheckCaptureSegment(handle, linkRequest.options());
            handle = SharedUtils.maybeInsertAllocator(fd, handle);
            return handle;
        });
    }

    protected abstract MethodHandle arrangeDowncall(MethodType inferredMethodType, FunctionDescriptor function, LinkerOptions options);

    @Override
    @CallerSensitive
    public final MemorySegment upcallStub(MethodHandle target, FunctionDescriptor function, Arena arena, Linker.Option... options) {
        Reflection.ensureNativeAccess(Reflection.getCallerClass(), Linker.class, "upcallStub");
        Objects.requireNonNull(arena);
        Objects.requireNonNull(target);
        Objects.requireNonNull(function);
        checkLayouts(function);
        SharedUtils.checkExceptions(target);
        function = stripNames(function);
        LinkerOptions optionSet = LinkerOptions.forUpcall(function, options);

        MethodType type = function.toMethodType();
        if (!type.equals(target.type())) {
            throw new IllegalArgumentException("Wrong method handle type: " + target.type());
        }

        UpcallStubFactory factory = UPCALL_CACHE.get(new LinkRequest(function, optionSet), linkRequest ->
            arrangeUpcall(type, linkRequest.descriptor(), linkRequest.options()));
        return factory.makeStub(target, arena);
    }

    protected abstract UpcallStubFactory arrangeUpcall(MethodType targetType, FunctionDescriptor function, LinkerOptions options);

    @Override
    public SystemLookup defaultLookup() {
        return SystemLookup.getInstance();
    }

    /** {@return byte order used by this linker} */
    protected abstract ByteOrder linkerByteOrder();

    // C spec mandates that variadic arguments smaller than int are promoted to int,
    // and float is promoted to double
    // See: https://en.cppreference.com/w/c/language/conversion#Default_argument_promotions
    // We reject the corresponding layouts here, to avoid issues where unsigned values
    // are sign extended when promoted. (as we don't have a way to unambiguously represent signed-ness atm).
    private void validateVariadicLayouts(FunctionDescriptor function, LinkerOptions optionSet) {
        if (optionSet.isVariadicFunction()) {
            List<MemoryLayout> argumentLayouts = function.argumentLayouts();
            List<MemoryLayout> variadicLayouts = argumentLayouts.subList(optionSet.firstVariadicArgIndex(), argumentLayouts.size());

            for (MemoryLayout variadicLayout : variadicLayouts) {
                if (variadicLayout.equals(ValueLayout.JAVA_BOOLEAN)
                    || variadicLayout.equals(ValueLayout.JAVA_BYTE)
                    || variadicLayout.equals(ValueLayout.JAVA_CHAR)
                    || variadicLayout.equals(ValueLayout.JAVA_SHORT)
                    || variadicLayout.equals(ValueLayout.JAVA_FLOAT)) {
                    throw new IllegalArgumentException("Invalid variadic argument layout: " + variadicLayout);
                }
            }
        }
    }

    private void checkLayouts(FunctionDescriptor descriptor) {
        descriptor.returnLayout().ifPresent(this::checkLayout);
        descriptor.argumentLayouts().forEach(this::checkLayout);
    }

    private void checkLayout(MemoryLayout layout) {
        // Note: we should not worry about padding layouts, as they cannot be present in a function descriptor
        if (layout instanceof SequenceLayout) {
            throw new IllegalArgumentException("Unsupported layout: " + layout);
        } else {
            checkLayoutRecursive(layout);
        }
    }

    private void checkLayoutRecursive(MemoryLayout layout) {
        if (layout instanceof ValueLayout vl) {
            checkSupported(vl);
        } else if (layout instanceof StructLayout sl) {
            checkHasNaturalAlignment(layout);
            long offset = 0;
            long lastUnpaddedOffset = 0;
            for (MemoryLayout member : sl.memberLayouts()) {
                // check element offset before recursing so that an error points at the
                // outermost layout first
                checkMemberOffset(sl, member, lastUnpaddedOffset, offset);
                checkLayoutRecursive(member);

                offset += member.byteSize();
                if (!(member instanceof PaddingLayout)) {
                    lastUnpaddedOffset = offset;
                }
            }
            checkGroupSize(sl, lastUnpaddedOffset);
        } else if (layout instanceof UnionLayout ul) {
            checkHasNaturalAlignment(layout);
            long maxUnpaddedLayout = 0;
            for (MemoryLayout member : ul.memberLayouts()) {
                checkLayoutRecursive(member);
                if (!(member instanceof PaddingLayout)) {
                    maxUnpaddedLayout = Long.max(maxUnpaddedLayout, member.byteSize());
                }
            }
            checkGroupSize(ul, maxUnpaddedLayout);
        } else if (layout instanceof SequenceLayout sl) {
            checkHasNaturalAlignment(layout);
            checkLayoutRecursive(sl.elementLayout());
        }
    }

    // check for trailing padding
    private static void checkGroupSize(GroupLayout gl, long maxUnpaddedOffset) {
        long expectedSize = Utils.alignUp(maxUnpaddedOffset, gl.byteAlignment());
        if (gl.byteSize() != expectedSize) {
            throw new IllegalArgumentException("Layout '" + gl + "' has unexpected size: "
                    + gl.byteSize() + " != " + expectedSize);
        }
    }

    // checks both that there is no excess padding between 'memberLayout' and
    // the previous layout
    private static void checkMemberOffset(StructLayout parent, MemoryLayout memberLayout,
                                          long lastUnpaddedOffset, long offset) {
        long expectedOffset = Utils.alignUp(lastUnpaddedOffset, memberLayout.byteAlignment());
        if (expectedOffset != offset) {
            throw new IllegalArgumentException("Member layout '" + memberLayout + "', of '" + parent + "'" +
                    " found at unexpected offset: " + offset + " != " + expectedOffset);
        }
    }

    private static void checkSupported(ValueLayout valueLayout) {
        valueLayout = valueLayout.withoutName();
        if (valueLayout instanceof AddressLayout addressLayout) {
            valueLayout = addressLayout.withoutTargetLayout();
        }
        if (!SUPPORTED_LAYOUTS.contains(valueLayout.withoutName())) {
            throw new IllegalArgumentException("Unsupported layout: " + valueLayout);
        }
    }

    private static void checkHasNaturalAlignment(MemoryLayout layout) {
        if (!((AbstractLayout<?>) layout).hasNaturalAlignment()) {
            throw new IllegalArgumentException("Layout alignment must be natural alignment: " + layout);
        }
    }

    private static MemoryLayout stripNames(MemoryLayout ml) {
        // we don't care about transferring alignment and byte order here
        // since the linker already restricts those such that they will always be the same
        return switch (ml) {
            case StructLayout sl -> MemoryLayout.structLayout(stripNames(sl.memberLayouts()));
            case UnionLayout ul -> MemoryLayout.unionLayout(stripNames(ul.memberLayouts()));
            case SequenceLayout sl -> MemoryLayout.sequenceLayout(sl.elementCount(), stripNames(sl.elementLayout()));
            case AddressLayout al -> al.targetLayout()
                    .map(tl -> al.withoutName().withTargetLayout(stripNames(tl)))
                    .orElseGet(al::withoutName);
            default -> ml.withoutName(); // ValueLayout and PaddingLayout
        };
    }

    private static MemoryLayout[] stripNames(List<MemoryLayout> layouts) {
        return layouts.stream()
                .map(AbstractLinker::stripNames)
                .toArray(MemoryLayout[]::new);
    }

    private static FunctionDescriptor stripNames(FunctionDescriptor function) {
        return function.returnLayout()
                .map(rl -> FunctionDescriptor.of(stripNames(rl), stripNames(function.argumentLayouts())))
                .orElseGet(() -> FunctionDescriptor.ofVoid(stripNames(function.argumentLayouts())));
    }

    private static final Set<MemoryLayout> SUPPORTED_LAYOUTS = Set.of(
            ValueLayout.JAVA_BOOLEAN,
            ValueLayout.JAVA_BYTE,
            ValueLayout.JAVA_CHAR,
            ValueLayout.JAVA_SHORT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_FLOAT,
            ValueLayout.JAVA_LONG,
            ValueLayout.JAVA_DOUBLE,
            ValueLayout.ADDRESS
    );
}
