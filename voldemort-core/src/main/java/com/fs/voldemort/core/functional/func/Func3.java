package com.fs.voldemort.core.functional.func;

import java.io.Serializable;

@FunctionalInterface
public interface Func3<T1, T2, T3,R> {
    R call(T1 t1, T2 t2, T3 t3);
}
