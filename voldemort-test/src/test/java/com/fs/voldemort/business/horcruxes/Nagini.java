package com.fs.voldemort.business.horcruxes;

import com.fs.voldemort.business.support.*;
import com.fs.voldemort.core.functional.action.Action2;

@LogicCell
public class Nagini{

    @BFunc
    public String func(String c1, String c2,
                       String cNull){
        return "This is Nagini! Target is c1:" + c1 + "c2:" + c2;
    }

}
