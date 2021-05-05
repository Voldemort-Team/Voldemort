package com.fs.voldemort.tcc.node;

import com.fs.voldemort.core.support.CallerParameter;

/**
 * TCC事务节点的接口
 */
public interface ITCCHandler {
    
    /**
     * 检查预留资源，无状态
     * @return
     */
    boolean goTry(CallerParameter parameter);

    /**
     * 提交操作，支持幂等，支持悬挂提交
     * @return
     */
    Object confirm();

    /**
     * 回滚操作，支持幂等，支持悬挂提交
     */
    void cancel();

}
