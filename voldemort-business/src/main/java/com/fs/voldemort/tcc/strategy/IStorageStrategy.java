package com.fs.voldemort.tcc.strategy;

import com.fs.voldemort.tcc.state.ITCCState;

public interface IStorageStrategy {

    void saveState(ITCCState state);
    
}
