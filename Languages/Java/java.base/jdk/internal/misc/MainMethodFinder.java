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

package jdk.internal.misc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class MainMethodFinder {
    private static boolean correctArgs(Method method) {
        int argc = method.getParameterCount();

        return argc == 0 || argc == 1 && method.getParameterTypes()[0] == String[].class;
    }

    /**
     * Gather all the "main" methods in the class hierarchy.
     *
     * @param refc         the main class or super class
     * @param mains        accumulated main methods
     * @param isMainClass  the class is the main class and not a super class
     */
    private static void gatherMains(Class<?> refc, List<Method> mains, boolean isMainClass) {
        if (refc != null && refc != Object.class) {
            for (Method method : refc.getDeclaredMethods()) {
                int mods = method.getModifiers();
                // Must be named "main", public|protected|package-private, not synthetic (bridge) and either
                // no arguments or one string array argument. Only statics in the Main class are acceptable.
                if ("main".equals(method.getName()) &&
                        !method.isSynthetic() &&
                        !Modifier.isPrivate(mods) &&
                        correctArgs(method) &&
                        (isMainClass || !Modifier.isStatic(mods)))
                {
                    mains.add(method);
                }
            }

            gatherMains(refc.getSuperclass(), mains, false);
        }
    }

    /**
     * Comparator for two methods.
     * Priority order is;
     * sub-class < super-class.
     * static < non-static,
     * string arg < no arg and
     *
     * @param a  first method
     * @param b  second method
     *
     * @return -1, 0 or 1 to represent higher priority. equals priority or lesser priority.
     */
    private static int compareMethods(Method a, Method b) {
        Class<?> aClass = a.getDeclaringClass();
        Class<?> bClass = b.getDeclaringClass();

        if (aClass != bClass) {
            if (bClass.isAssignableFrom(aClass)) {
                return -1;
            } else {
                return 1;
            }
        }

        int aMods = a.getModifiers();
        int bMods = b.getModifiers();
        boolean aIsStatic = Modifier.isStatic(aMods);
        boolean bIsStatic = Modifier.isStatic(bMods);

        if (aIsStatic && !bIsStatic) {
            return -1;
        } else if (!aIsStatic && bIsStatic) {
            return 1;
        }

        int aCount = a.getParameterCount();
        int bCount = b.getParameterCount();

        if (bCount < aCount) {
            return -1;
        } else if (aCount < bCount) {
            return 1;
        }

        return 0;
    }

    /**
     * Return the traditional main method or null if not found.
     *
     * @param mainClass main class
     *
     * @return main method or null
     */
    private static Method getTraditionalMain(Class<?> mainClass) {
        try {
            Method traditionalMain = mainClass.getMethod("main", String[].class);
            int mods = traditionalMain.getModifiers();

            if (Modifier.isStatic(mods) && Modifier.isPublic(mods) && traditionalMain.getReturnType() == void.class) {
                return traditionalMain;
            }
        } catch (NoSuchMethodException ex) {
            // not found
        }

        return null;
    }

    /**
     * {@return priority main method if none found}
     *
     * @param mainClass main class
     *
     * @throws NoSuchMethodException when not preview and no method found
     */
    public static Method findMainMethod(Class<?> mainClass) throws NoSuchMethodException {
        boolean isTraditionMain = !PreviewFeatures.isEnabled();
        if (isTraditionMain) {
            return mainClass.getMethod("main", String[].class);
        }

        List<Method> mains = new ArrayList<>();
        gatherMains(mainClass, mains, true);

        if (mains.isEmpty()) {
            throw new NoSuchMethodException("No main method found");
        }

        if (1 < mains.size()) {
            mains.sort(MainMethodFinder::compareMethods);
        }

        Method mainMethod = mains.get(0);
        Method traditionalMain = getTraditionalMain(mainClass);

        if (traditionalMain != null && !traditionalMain.equals(mainMethod)) {
            System.err.println("WARNING: \"" + mains.get(0) + "\" chosen over \"" + traditionalMain + "\"");
        }

        return mains.get(0);
    }
}
