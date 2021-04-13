package com.fs.voldemort.tcc.exception;

import com.fs.voldemort.core.support.CallerNode;
import com.fs.voldemort.core.support.CallerParameter;

public class ExecuteCallerNodeException extends RuntimeException {

    private static final long serialVersionUID = 8874801524460382873L;

    private CallerParameter parameter;

    private CallerNode node;

    private String name;

    public ExecuteCallerNodeException(String message, Throwable e, CallerNode node, CallerParameter parameter) {
        super(message, e);
        this.node = node;
        this.parameter = parameter;
    }

    public CallerNode getNode() {
        return node;
    }

    public CallerParameter getParameter() {
        return parameter;
    }

    public String getName() {
        return name;
    }
    
}