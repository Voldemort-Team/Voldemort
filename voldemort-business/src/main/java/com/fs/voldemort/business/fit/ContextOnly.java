package com.fs.voldemort.business.fit;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface ContextOnly {
    FitArg f_getArg = (clazz, name, fitContext) -> fitContext.getBean(clazz,name);
}
