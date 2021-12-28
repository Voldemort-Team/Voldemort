package com.fs.voldemort.business.fit;

import com.fs.voldemort.business.BFuncParameter;
import com.fs.voldemort.business.paramfinder.ParamFindResult;
import com.fs.voldemort.business.paramfinder.ParamFinderLibrary;
import com.fs.voldemort.business.support.BFunc;
import com.fs.voldemort.business.util.ConstructorHolder;
import com.fs.voldemort.core.exception.CrucioException;
import com.fs.voldemort.core.functional.func.Func1;
import com.fs.voldemort.core.functional.func.Func2;
import com.fs.voldemort.core.support.CallerParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class FitLibrary {

    public static final Object[] EMPTY_RESULT = new Object[0];

    public static final Func1<Class<?>,Method> f_obtainMethod = clazz -> {
        final List<Method> funcMethodList = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                        .anyMatch(annotation -> annotation.annotationType().equals(BFunc.class)))
                .collect(Collectors.toList());

        if(funcMethodList.size() > 1) {
            throw new CrucioException("Settle function can only have one func method!");
        }else if(funcMethodList.isEmpty()) {
            return null;
        }

        return funcMethodList.get(0);
    };

    public static final Func2<Class<?>, CallerParameter, Object[]> f_logicCellAutoFit = (clazz, callerParameter) ->{

        final BFuncParameter param = (BFuncParameter)callerParameter;

        final Method funcMethod = f_obtainMethod.call(clazz);
        if(funcMethod == null)
            return EMPTY_RESULT;

        final List<PArg> resultArgs = ParamFinderLibrary.f_MethodParamFinder.getParam(funcMethod).stream().map(arg -> {
            Object value = param.getParameter(arg.getParamName());
            if (value == null) {
                value = param.context().get(arg.getParamName());
            }
            return value == null ? new PArg(arg.getParamName(), arg.getParamClazz())
                    : new PArg(arg.getParamName(), arg.getParamClazz(), value);
        }).collect(Collectors.toList());
        return resultArgs.stream().map(pArg -> pArg.value).toArray();
    };

    public static final Func2<Class<?>, CallerParameter, Object[]> f_logicCellCustomFit = (clazz, param) ->{

        final Method funcMethod = f_obtainMethod.call(clazz);
        if(funcMethod == null)
            return EMPTY_RESULT;

        //Get custom class
        final IFit iFit = ConstructorHolder.getNew(funcMethod.getAnnotation(BFunc.class).iFit());
        if(iFit == null)
            throw new CrucioException("Please check custom fit func!");

        return iFit.fit(funcMethod,param);
    };

    private static final Collection<Class> uniqueCheckFitAnnotations = new HashSet() {{
        add(ContextOnly.class);
        add(ContainerOnly.class);
        add(AutoFit.class);
    }};

    public static final Func2<Collection<ParamFindResult>, FitContext, Collection<?>> f_lambdaFit = (paramFindResults,fitContext) -> {
        
        final Collection args = new LinkedList<>();

        for (ParamFindResult paramFindResult : paramFindResults) {

            //Arg fix func
            FitArg f_fitArg = null;

            //Arg default
            String defaultValue = null;

            //Unique annotation check flag
            boolean hasExplicitUniqueAnnotation = false;

            for (Annotation annotation : paramFindResult.getAnnotation()) {
                //Unique check
                if(hasExplicitUniqueAnnotation) {
                    throw new FitException("Multiple unique annotations declared, please check!");
                }

                if(uniqueCheckFitAnnotations.contains(annotation.annotationType())) {
                    hasExplicitUniqueAnnotation = true;
                    //Get arg fix func TODO:动态获取优化
                    if(annotation instanceof ContextOnly) {
                        f_fitArg = ContextOnly.f_getArg;
                    }else if(annotation instanceof ContainerOnly){
                        f_fitArg = ContainerOnly.f_getArg;
                    }else if(annotation instanceof AutoFit){
                        f_fitArg = AutoFit.f_getArg;
                    }
                    continue;
                }

                //Get default value
                if(annotation instanceof Default) {
                    defaultValue = ((Default)annotation).value();
                }
            }

            //Get real arg
            if(f_fitArg == null) {
                f_fitArg = AutoFit.f_getArg;
            }

            Object arg = f_fitArg.getParam(paramFindResult,fitContext);
            if(arg == null && defaultValue != null) {
                //TODO:默认值转换约束检查
                arg = defaultValue;
            }
            args.add(arg);
        }
        return args;
    };

}
