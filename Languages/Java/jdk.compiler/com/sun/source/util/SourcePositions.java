/*
 * Copyright (c) 2005, 2021, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.source.util;

import com.sun.source.tree.*;

/**
 * Provides methods to obtain the position of a Tree within a CompilationUnit.
 * A position is defined as a simple character offset from the start of a
 * CompilationUnit where the first character is at offset 0.
 *
 * @author Peter von der Ah&eacute;
 * @since 1.6
 */
public interface SourcePositions {

    /**
     * Returns the starting position of tree within file.  If tree is not found within
     * file, or if the starting position is not available,
     * returns {@link javax.tools.Diagnostic#NOPOS}.
     * The returned position must be at the start of the yield of this tree, that
     * is for any sub-tree of this tree, the following must hold:
     *
     * <p>
     * {@code getStartPosition(file, tree) <= getStartPosition(file, subtree)} or <br>
     * {@code getStartPosition(file, tree) == NOPOS} or <br>
     * {@code getStartPosition(file, subtree) == NOPOS}
     * </p>
     *
     * @param file CompilationUnit in which to find tree
     * @param tree tree for which a position is sought
     * @return the start position of tree
     */
     long getStartPosition(CompilationUnitTree file, Tree tree);

    /**
     * Returns the ending position of tree within file.  If tree is not found within
     * file, or if the ending position is not available,
     * returns {@link javax.tools.Diagnostic#NOPOS}.
     * The returned position must be at the end of the yield of this tree,
     * that is for any sub-tree of this tree, the following must hold:
     *
     * <p>
     * {@code getEndPosition(file, tree) >= getEndPosition(file, subtree)} or <br>
     * {@code getEndPosition(file, tree) == NOPOS} or <br>
     * {@code getEndPosition(file, subtree) == NOPOS}
     * </p>
     *
     * In addition, the following must hold:
     *
     * <p>
     * {@code getStartPosition(file, tree) <= getEndPosition(file, tree)} or <br>
     * {@code getStartPosition(file, tree) == NOPOS} or <br>
     * {@code getEndPosition(file, tree) == NOPOS}
     * </p>
     *
     * @param file CompilationUnit in which to find tree
     * @param tree tree for which a position is sought
     * @return the end position of tree
     */
     long getEndPosition(CompilationUnitTree file, Tree tree);

}
