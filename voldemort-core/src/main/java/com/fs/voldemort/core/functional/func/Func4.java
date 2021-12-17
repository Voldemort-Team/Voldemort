package com.fs.voldemort.core.functional.func;

import java.io.Serializable;

@FunctionalInterface
public interface Func4<T1, T2, T3, T4, R> extends Serializable {
    R call(T1 t1, T2 t2, T3 t3, T4 t4);
}
