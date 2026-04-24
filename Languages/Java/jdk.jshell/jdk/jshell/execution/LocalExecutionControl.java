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
package jdk.jshell.execution;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import jdk.internal.classfile.Classfile;
import jdk.internal.classfile.ClassTransform;
import jdk.internal.classfile.instruction.BranchInstruction;

/**
 * An implementation of {@link jdk.jshell.spi.ExecutionControl} which executes
 * in the same JVM as the JShell-core.
 *
 * @author Grigory Ptashko
 * @since 9
 */
public class LocalExecutionControl extends DirectExecutionControl {

    private final Object STOP_LOCK = new Object();
    private boolean userCodeRunning = false;
    private ThreadGroup execThreadGroup;
    private Field allStop = null;

    /**
     * Creates an instance, delegating loader operations to the specified
     * delegate.
     *
     * @param loaderDelegate the delegate to handle loading classes
     */
    public LocalExecutionControl(LoaderDelegate loaderDelegate) {
        super(loaderDelegate);
    }

    /**
     * Create an instance using the default class loading.
     */
    public LocalExecutionControl() {
    }

    @Override
    public void load(ClassBytecodes[] cbcs)
            throws ClassInstallException, NotImplementedException, EngineTerminationException {
        super.load(Stream.of(cbcs)
                .map(cbc -> new ClassBytecodes(cbc.name(), instrument(cbc.bytecodes())))
                .toArray(ClassBytecodes[]::new));
    }

    private static final String CANCEL_CLASS = "REPL.$Cancel$";
    private static final ClassDesc CD_Cancel = ClassDesc.of(CANCEL_CLASS);
    private static final ClassDesc CD_ThreadDeath = ClassDesc.of("java.lang.ThreadDeath");
    private static final MethodTypeDesc MTD_void = MethodTypeDesc.of(ConstantDescs.CD_void);

    private static byte[] instrument(byte[] classFile) {
        return Classfile.parse(classFile)
                        .transform(ClassTransform.transformingMethodBodies((cob, coe) -> {
                            if (coe instanceof BranchInstruction)
                                cob.invokestatic(CD_Cancel, "stopCheck", MTD_void);
                            cob.with(coe);
                        }));
    }

    private static ClassBytecodes genCancelClass() {
        return new ClassBytecodes(CANCEL_CLASS, Classfile.build(CD_Cancel, clb ->
             clb.withFlags(Classfile.ACC_PUBLIC)
                .withField("allStop", ConstantDescs.CD_boolean, Classfile.ACC_PUBLIC | Classfile.ACC_STATIC | Classfile.ACC_VOLATILE)
                .withMethodBody("stopCheck", MTD_void, Classfile.ACC_PUBLIC | Classfile.ACC_STATIC, cob ->
                        cob.getstatic(CD_Cancel, "allStop", ConstantDescs.CD_boolean)
                           .ifThenElse(tb -> tb.new_(CD_ThreadDeath)
                                               .dup()
                                               .invokespecial(CD_ThreadDeath, "<init>", MTD_void)
                                               .athrow(),
                                       eb -> eb.return_()))));
    }

    @Override
    @SuppressWarnings("removal")
    protected String invoke(Method doitMethod) throws Exception {
        if (allStop == null) {
            super.load(new ClassBytecodes[]{ genCancelClass() });
            allStop = findClass(CANCEL_CLASS).getDeclaredField("allStop");
        }
        allStop.set(null, false);

        execThreadGroup = new ThreadGroup("JShell process local execution");

        AtomicReference<InvocationTargetException> iteEx = new AtomicReference<>();
        AtomicReference<IllegalAccessException> iaeEx = new AtomicReference<>();
        AtomicReference<NoSuchMethodException> nmeEx = new AtomicReference<>();
        AtomicReference<Boolean> stopped = new AtomicReference<>(false);

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (e instanceof InvocationTargetException) {
                if (e.getCause() instanceof ThreadDeath) {
                    stopped.set(true);
                } else {
                    iteEx.set((InvocationTargetException) e);
                }
            } else if (e instanceof IllegalAccessException) {
                iaeEx.set((IllegalAccessException) e);
            } else if (e instanceof NoSuchMethodException) {
                nmeEx.set((NoSuchMethodException) e);
            } else if (e instanceof ThreadDeath) {
                stopped.set(true);
            }
        });

        final Object[] res = new Object[1];
        Thread snippetThread = new Thread(execThreadGroup, () -> {
            try {
                res[0] = doitMethod.invoke(null, new Object[0]);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof ThreadDeath) {
                    stopped.set(true);
                } else {
                    iteEx.set(e);
                }
            } catch (IllegalAccessException e) {
                iaeEx.set(e);
            } catch (ThreadDeath e) {
                stopped.set(true);
            }
        });

        snippetThread.start();
        Thread[] threadList = new Thread[execThreadGroup.activeCount()];
        execThreadGroup.enumerate(threadList);
        for (Thread thread : threadList) {
            if (thread != null) {
                thread.join();
            }
        }

        if (stopped.get()) {
            throw new StoppedException();
        }

        if (iteEx.get() != null) {
            throw iteEx.get();
        } else if (nmeEx.get() != null) {
            throw nmeEx.get();
        } else if (iaeEx.get() != null) {
            throw iaeEx.get();
        }

        return valueString(res[0]);
    }

    @Override
    public void stop() throws EngineTerminationException, InternalException {
        synchronized (STOP_LOCK) {
            if (!userCodeRunning) {
                return;
            }
            if (execThreadGroup == null) {
                throw new InternalException("Process-local code snippets thread group is null. Aborting stop.");
            }
            try {
                allStop.set(null, true);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new InternalException("Exception on local stop: " + ex);
            }
            Thread[] threads;
            int len, threadCount;
            do {
                len = execThreadGroup.activeCount() + 4;
                threads = new Thread[len];
                threadCount = execThreadGroup.enumerate(threads);
            } while (threadCount == len);
            for (int i = 0; i < threadCount; i++) {
                threads[i].interrupt();
            }
        }
    }

    @Override
    protected void clientCodeEnter() {
        synchronized (STOP_LOCK) {
            userCodeRunning = true;
        }
    }

    @Override
    protected void clientCodeLeave() {
        synchronized (STOP_LOCK) {
            userCodeRunning = false;
        }
    }

}
