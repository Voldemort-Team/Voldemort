package com.fs.voldemort.business.support;

import com.fs.voldemort.core.exception.CallerException;
import com.fs.voldemort.core.support.CallerContext;
import com.fs.voldemort.core.support.CallerParameter;
import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author frank
 */
public interface BFuncCallable {

    /**
     * #Definition
     *  R func(
     *      Part 1-> Result arg
     *      Part 2-> Context arg
     *      Part 3-> Operate function
     *  )
     *
     * #Part1  & Part2
     * Get arg info of target function method, and adapt result that has been executed by last function:
     * ## Role 1
     *     Last function    call    Current function
     *     R func1()        --->    func2(R arg)
     * ## Role 2
     *     Last function    call    Current function
     *     R func1()        --->    func2(R arg,   C1 arg1, C2 arg2, C2 arg3)
     *  ### Parameter description
     *     R arg                    -> The result of last function
     *     C1 arg1  context param   -> The arg of context param
     *                                 Context param class is C1.class
     *                                 Context param key is     'arg1'
     *     C2 arg2                  -> Class: C1.class      key:'arg2'
     *     C2 arg3                  -> Class: C2.class      key:'arg3'
     *
     * #Part3
     * The above has been described the role which fill the result and parameters into the current function,
     * but there is still such a scene, developer need to manipulate context parameters in the function, that`s
     * why BFunc provides the entry of the operation function in the formal parameter;
     * For example base on above # role 2 current function:
     *  func2(R arg,  C1 arg1, C2 arg2, C2 arg3, {@link BFuncOperate} Func2<String,Object,Boolean> f_setC)
     *
     */
    default Object[] paramFit(@NonNull final CallerParameter p) {

        final List<Method> funcMethodList = Arrays.stream(getClass().getDeclaredMethods())
            .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                    .anyMatch(annotation -> annotation.annotationType().equals(BFunc.class)))
            .collect(Collectors.toList());

        if(funcMethodList.size() > 1) {
            throw new CallerException("Settle function can only have one func method!");
        }else if(funcMethodList.isEmpty()) {
            return new Object[0];
        }

        final Method funcMethod = funcMethodList.get(0);

        final Set<Object> arg = new HashSet<>();
        final Set<CArg> cArgSet = new HashSet<>();
        final Set<PArg> pArgSet = Arrays.stream(funcMethod.getParameters()).filter(param->{
            if(param.isAnnotationPresent(BFuncOperate.class)){
                cArgSet.add(new CArg(param.getAnnotation(BFuncOperate.class).value(),p.context()));
                return false;
            }
            return true;
        }).map(param-> new PArg(param.getName())).collect(Collectors.toSet());

        //Deal param arg
        if(!pArgSet.isEmpty()) {
            final PArg resultArg = pArgSet.iterator().next();
            resultArg.value = p.result;

            //Deal context arg
            if(pArgSet.size()>1) {
                pArgSet.stream().skip(1).forEach(pArg -> pArg.value = p.context().get(pArg.name));
            }
            arg.addAll(pArgSet.stream().map(pArg -> pArg.value).collect(Collectors.toSet()));
        }

        //Deal context operator func arg
        if(!cArgSet.isEmpty()) {
            arg.addAll(cArgSet.stream().map(CArg::getOperFunc).collect(Collectors.toSet()));
        }

        return arg.toArray();
    }

    default void setC(String k ,Object v){}

    /**
     * Param arg
     */
    class PArg {
        public final String name;
        public Object value;

        public PArg(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            var args = (PArg) o;
            return name.equals(args.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    /**
     * Func arg for context
     */
    class CArg {
        public final BFuncOperate.Oper oper;
        public final CallerContext context;

        public CArg(@NonNull BFuncOperate.Oper oper, @NonNull CallerContext context) {
            this.oper = oper;
            this.context = context;
        }

        public Object getOperFunc() {
            return oper.getFunc.call(context);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            var args = (CArg) o;
            return oper.equals(args.oper);
        }

        @Override
        public int hashCode() {
            return Objects.hash(oper);
        }
    }

}
