package com.fs.voldemort.business.paramfinder.util;

import com.fs.voldemort.business.paramfinder.ParamFindResult;
import com.fs.voldemort.business.paramfinder.SimpleFindResult;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;

public class JavassistUtil {

    public static Collection<ParamFindResult> getParam(final Method method) throws NotFoundException, ClassNotFoundException{
        if(method == null) {
            throw new IllegalArgumentException("Method Cannot be null!");
        }
        final String methodName = method.getName();
        final Class clazz = method.getDeclaringClass();

        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get(clazz.getName());
        CtMethod cm = cc.getDeclaredMethod(methodName);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            throw new NotFoundException("cannot get LocalVariableAttribute");
        }

        TypeAnnotationsAttribute typeAnnotationsAttribute = (TypeAnnotationsAttribute)methodInfo.getAttribute(TypeAnnotationsAttribute.visibleTag);
        System.out.println(typeAnnotationsAttribute.numAnnotations());
        System.out.println(typeAnnotationsAttribute.getName());
        System.out.println(typeAnnotationsAttribute.getConstPool().getUtf8Info(57));


        Collection<ParamFindResult> params = new LinkedList<>();
        int staticPos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        CtClass[] ctClazzes = cm.getParameterTypes();

        int indexRelatively = 0;
        for (CtClass paramClazz : ctClazzes) {
            params.add(new SimpleFindResult(attr.variableName(indexRelatively++ + staticPos), Class.forName(paramClazz.getName())));
        }
        return params;
    }
}
