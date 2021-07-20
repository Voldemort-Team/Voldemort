package com.fs.voldemort.tcc.simple;

import com.fs.voldemort.tcc.TCCManager;
import com.fs.voldemort.tcc.simple.adapter.SimpleCancelCompensateAdapter;
import com.fs.voldemort.tcc.simple.adapter.SimpleStateManagerAdapter;
import com.fs.voldemort.tcc.simple.service.biz.SimpleTCCCancelRetryBiz;
import com.fs.voldemort.tcc.simple.service.biz.SimpleTCCBeginBiz;
import com.fs.voldemort.tcc.simple.service.biz.SimpleTCCConfirmRetryBiz;
import com.fs.voldemort.tcc.simple.service.biz.SimpleTCCEndBiz;
import com.fs.voldemort.tcc.simple.service.biz.SimpleTCCUpdateBiz;
import com.fs.voldemort.tcc.simple.service.gear.IBusinessSupportGear;
import com.fs.voldemort.tcc.simple.service.gear.IRepositoryGear;
import com.fs.voldemort.tcc.simple.service.gear.ISerializeGear;
import com.fs.voldemort.tcc.simple.adapter.SimpleConfirmCompensateAdapter;

public class SimpleTCCManager extends TCCManager {

    private SimpleTCCManager() {
    }


    public static SimpleTCCManagerBuilder builder() {
        return new SimpleTCCManagerBuilder();
    }

    public static SimpleTCCManagerExtendBuilder extendBuilder() {
        return new SimpleTCCManagerExtendBuilder();
    }

    public static class SimpleTCCManagerBuilder {

        private SimpleTCCManagerBuilder() {

        }

        private IRepositoryGear repositoryGear;
        
        private ISerializeGear serializeGear;

        private IBusinessSupportGear businessSupportGear;

        public SimpleTCCManagerBuilder setRepositoryGear(IRepositoryGear repositoryGear) {
            this.repositoryGear = repositoryGear;
            return this;
        }

        public SimpleTCCManagerBuilder setSerializeGear(ISerializeGear serializeGear) {
            this.serializeGear = serializeGear;
            return this;
        }

        public SimpleTCCManagerBuilder setBusinessSupportGear(IBusinessSupportGear businessSupportGear) {
            this.businessSupportGear = businessSupportGear;
            return this;
        }

        public SimpleTCCManager build() {

            SimpleTCCManager simpleTCCManager = new SimpleTCCManager();
            simpleTCCManager.setStateManager(
                new SimpleStateManagerAdapter(
                    new SimpleTCCBeginBiz(repositoryGear, serializeGear, businessSupportGear), 
                    new SimpleTCCUpdateBiz(repositoryGear, serializeGear, businessSupportGear), 
                    new SimpleTCCEndBiz(repositoryGear, serializeGear, businessSupportGear))
            );
            simpleTCCManager.setConfirmCompensateStrategy(
                new SimpleConfirmCompensateAdapter(
                    new SimpleTCCConfirmRetryBiz(repositoryGear, serializeGear, businessSupportGear)
                )
            );
            simpleTCCManager.setCancelCompensateStrategy(
                new SimpleCancelCompensateAdapter(
                    new SimpleTCCCancelRetryBiz(repositoryGear, serializeGear, businessSupportGear)
                )
            );
            return simpleTCCManager;
        }

    }
    

    public static class SimpleTCCManagerExtendBuilder extends SimpleTCCManagerBuilder {

        private SimpleTCCManagerExtendBuilder() {

        }

        private SimpleTCCBeginBiz tccBeginBiz;

        private SimpleTCCUpdateBiz tccUpdateBiz;

        private SimpleTCCEndBiz tccEndBiz;

        private SimpleTCCConfirmRetryBiz tccConfirmRetryBiz;

        private SimpleTCCCancelRetryBiz tccCancelRetryBiz;

        public SimpleTCCManagerExtendBuilder setTCCBeginBiz(SimpleTCCBeginBiz beginBiz) {
            this.tccBeginBiz = beginBiz;
            return this;
        }

        public SimpleTCCManagerExtendBuilder setTCCUpdateBiz(SimpleTCCUpdateBiz updateBiz) {
            this.tccUpdateBiz = updateBiz;
            return this;
        }

        public SimpleTCCManagerExtendBuilder setTCCEndBiz(SimpleTCCEndBiz endBiz) {
            this.tccEndBiz = endBiz;
            return this;
        }

        public SimpleTCCManagerExtendBuilder setTCCConfirmRetryBiz(SimpleTCCConfirmRetryBiz confirmRetryBiz) {
            this.tccConfirmRetryBiz = confirmRetryBiz;
            return this;
        }

        public SimpleTCCManagerExtendBuilder setTCCCancelRetryBiz(SimpleTCCCancelRetryBiz cancelRetryBiz) {
            this.tccCancelRetryBiz = cancelRetryBiz;
            return this;
        }


        public SimpleTCCManager build() {
            SimpleTCCManager simpleTCCManager = new SimpleTCCManager();
            simpleTCCManager.setStateManager(
                new SimpleStateManagerAdapter(tccBeginBiz, tccUpdateBiz, tccEndBiz)
            );
            simpleTCCManager.setConfirmCompensateStrategy(
                new SimpleConfirmCompensateAdapter(tccConfirmRetryBiz)
            );
            simpleTCCManager.setCancelCompensateStrategy(
                new SimpleCancelCompensateAdapter(tccCancelRetryBiz)
            );
            return simpleTCCManager;
        }

    }
}
