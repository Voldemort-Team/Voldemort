package com.fs.voldemort.tcc.simple.service.biz;

import com.fs.voldemort.core.functional.action.Action1;
import com.fs.voldemort.tcc.node.BaseTCCHandler;
import com.fs.voldemort.tcc.simple.service.gear.IBusinessSupportGear;
import com.fs.voldemort.tcc.simple.service.gear.IRepositoryGear;
import com.fs.voldemort.tcc.simple.service.gear.ISerializeGear;
import com.fs.voldemort.tcc.state.ITCCState;

public class SimpleTCCCancelRetryBiz extends BaseTCCBiz implements Action1<ITCCState> {

    public SimpleTCCCancelRetryBiz(
        IRepositoryGear repositoryGear, 
        ISerializeGear serializeGear) {

        super(repositoryGear, serializeGear);

    }

    public SimpleTCCCancelRetryBiz(
        IRepositoryGear repositoryGear, 
        ISerializeGear serializeGear,
        IBusinessSupportGear businessSupportGear) {
        super(repositoryGear, serializeGear, businessSupportGear);
    }

    @Override
    public void apply(ITCCState state) {
        // TODO TCC 取消阶段 重试

        System.out.println("TCC: " + state.identify() + ", retry start...");

        state.getTriedNodeList().forEach(n -> {
            BaseTCCHandler tccHandler = (BaseTCCHandler) n.getTCCHandler();
            System.out.println(tccHandler.getName() + " cancel success.");
        });
        
        System.out.println("TCC: " + state.identify() + ", retry end...");
        
    }
    
}
