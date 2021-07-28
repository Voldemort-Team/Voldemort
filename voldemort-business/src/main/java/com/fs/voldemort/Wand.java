package com.fs.voldemort;

import com.fs.voldemort.business.BFuncCaller;
import java.util.concurrent.ThreadPoolExecutor;

import com.fs.voldemort.core.Caller;
import com.fs.voldemort.core.functional.func.Func0;
import com.fs.voldemort.core.support.CallerParameter;
import com.fs.voldemort.parallel.ParallelCaller;
import com.fs.voldemort.tcc.TCCCaller;
import com.fs.voldemort.tcc.TCCManager;
import com.fs.voldemort.tcc.node.TCCNodeParameter;

public abstract class Wand {

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
    
}
