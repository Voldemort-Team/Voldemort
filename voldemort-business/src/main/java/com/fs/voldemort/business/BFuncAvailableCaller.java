package com.fs.voldemort.business;

import com.fs.voldemort.core.Caller;
import com.fs.voldemort.core.functional.func.Func1;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * @author frank
 */
public class BFuncAvailableCaller extends Caller<Object> {

    protected static Func1<Class<?>, BFunc> getFunc;

    public static void initByAnnotation(Func1<Class<? extends Annotation>, Collection<Object>> getBusinessFuncHorcruxesFunc) {
        getFunc = new BFuncContainer().init(getBusinessFuncHorcruxesFunc).getFunc();
    }

    public static void init(Collection<Object> businessFuncHorcruxesFuncs) {
        getFunc = new BFuncContainer().init(businessFuncHorcruxesFuncs).getFunc();
    }

}
