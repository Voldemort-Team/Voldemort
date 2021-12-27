package com.fs.voldemort.business.fit;

import com.fs.voldemort.business.paramfinder.ParamFindResult;
import com.fs.voldemort.business.paramfinder.ParamFinderException;
import com.fs.voldemort.core.functional.func.Func1;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface FitArg{
    Object getParam(ParamFindResult paramFindResult, FitContext fitContext) throws ParamFinderException;
}
