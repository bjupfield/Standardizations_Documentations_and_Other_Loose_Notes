/*
 *  Copyright (c) 2019, 2023, Oracle and/or its affiliates. All rights reserved.
 *  ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 *
 */
package jdk.internal.foreign.layout;

import java.lang.foreign.PaddingLayout;
import java.util.Objects;
import java.util.Optional;

public final class PaddingLayoutImpl extends AbstractLayout<PaddingLayoutImpl> implements PaddingLayout {

    private PaddingLayoutImpl(long byteSize) {
        this(byteSize, 1, Optional.empty());
    }

    private PaddingLayoutImpl(long byteSize, long byteAlignment, Optional<String> name) {
        super(byteSize, byteAlignment, name);
    }

    @Override
    public String toString() {
        return decorateLayoutString("x" + byteSize());
    }

    @Override
    public boolean equals(Object other) {
        return this == other ||
                other instanceof PaddingLayoutImpl otherPadding &&
                super.equals(other) &&
                byteSize() == otherPadding.byteSize();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), byteSize());
    }

    @Override
    PaddingLayoutImpl dup(long byteAlignment, Optional<String> name) {
        return new PaddingLayoutImpl(byteSize(), byteAlignment, name);
    }

    @Override
    public boolean hasNaturalAlignment() {
        return true;
    }

    public static PaddingLayout of(long byteSize) {
        return new PaddingLayoutImpl(byteSize);
    }

}
