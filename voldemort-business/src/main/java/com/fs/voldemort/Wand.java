package com.fs.voldemort;

import com.fs.voldemort.business.BFuncCaller;
import java.util.concurrent.ThreadPoolExecutor;

import com.fs.voldemort.core.Caller;
import com.fs.voldemort.core.functional.action.Action1;
import com.fs.voldemort.core.functional.func.Func0;
import com.fs.voldemort.core.functional.func.Func1;
import com.fs.voldemort.core.functional.func.Func2;
import com.fs.voldemort.core.support.CallerParameter;
import com.fs.voldemort.parallel.ParallelCaller;
import com.fs.voldemort.tcc.TCCCaller;
import com.fs.voldemort.tcc.TCCManager;
import com.fs.voldemort.tcc.node.TCCNodeParameter;

public abstract class Wand {

    private Wand() {}

    public static CallerWand<?> create() {
        return new CallerWand<CallerWand<?>>(Caller.create(), null);
    }

    //#region Caller

    public static Caller caller() {
        return Caller.create();
    }

    public static Caller caller(CallerParameter initParameter) {
        return new Caller(initParameter);
    }

    public static Caller callerAndContext(CallerParameter initParameter) {
        return new Caller(initParameter, true);
    }

    //#endregion

    //#region BusinessCaller

    public static BFuncCaller businessCaller() {
        return BFuncCaller.create();
    }

    //#endregion

    //#region TCCCaller

    public static TCCCaller tccCaller(TCCManager tccManager) {
        return TCCCaller.create(tccManager);
    }

    public static TCCCaller tccCaller(TCCManager tccManager, Object param) {
        TCCCaller tccCaller = TCCCaller.create(tccManager);
        return (TCCCaller) tccCaller.call(p -> {
            ((TCCNodeParameter) p).getTCCState().setParam(param);
            return null;
        });
    }

    //#endregion

    //#region ParallelCaller

    public static ParallelCaller parallelCaller() {
        return ParallelCaller.create();
    }

    public static ParallelCaller parallelCaller(final Func0<ThreadPoolExecutor> executorFactoryFunc) {
        return ParallelCaller.createWithExecutor(executorFactoryFunc);
    }

    //#endregion
    
    public static class WandBuilder<P extends CallerWand<?>> {

        private final P parentWand;

        public WandBuilder(P parentWand) {
            this.parentWand = parentWand;
        }

        public CallerWand<P> caller() {
            return new CallerWand<P>(new Caller(), parentWand);
        }

        public ParallelWand<P> parallel() {
            return new ParallelWand<P>(new ParallelCaller(), parentWand);
        }

        public ParallelWand<P> parallel(final Func0<ThreadPoolExecutor> executorFactoryFunc) {
            return new ParallelWand<P>(new ParallelCaller(executorFactoryFunc), parentWand);
        }

        public ParallelWand<P> parallel(final int capacity, final Func2<Integer, Integer, ThreadPoolExecutor> executorFactoryFunc) {
            return new ParallelWand<P>(new ParallelCaller(capacity, executorFactoryFunc), parentWand);
        }

        public BusinessWand<P> business() {
            return new BusinessWand<P>(new BFuncCaller(), parentWand);
        }

    }

    public static class CallerWand<P extends CallerWand<?>> {

        private final P parentWand;
        private final Caller caller;

        protected CallerWand(Caller caller, P parentWand) {
            this.caller = caller;
            this.parentWand = parentWand;
        }

        public CallerWand<P> call(Func1<CallerParameter, Object> func) {
            caller.call(func);
            return this;
        }

        public CallerWand<P> call(Caller subCaller) {
            caller.call(subCaller);
            return this;
        }

        public void exec(Action1<Object> action) {
            caller.exec(action);
        }

        public WandBuilder<?> sub() {
            return new WandBuilder<CallerWand<P>>(this);
        }

        public P end() {
            if(parentWand != null) {
                parentWand.call(get());
            }
            return parentWand;
        }

        public Caller get() {
            return caller;
        }

        @SuppressWarnings("unchecked")
        protected <C> C caller() {
            return (C) caller;
        }

        protected P parent() {
            return parentWand;
        }

    }

    public static class ParallelWand<P extends CallerWand<?>> extends CallerWand<P> {

        protected ParallelWand(ParallelCaller parallelCaller, P parentWand) {
            super(parallelCaller, parentWand);
        }

        @Override
        public WandBuilder<?> sub() {
            return new WandBuilder<ParallelWand<P>>(this);
        }
        
    }

    public static class BusinessWand<P extends CallerWand<?>> extends CallerWand<P> {

        protected BusinessWand(BFuncCaller caller, P parentWand) {
            super(caller, parentWand);
        }

        public BusinessWand<P> call(Class<?> funcClazz) {
            BFuncCaller caller = caller();
            caller.call(funcClazz);
            return this;
        }

        @Override
        public WandBuilder<?> sub() {
            return new WandBuilder<BusinessWand<P>>(this);
        }

    }


}
