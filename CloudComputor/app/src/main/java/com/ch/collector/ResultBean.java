package com.ch.collector;

import java.io.Serializable;

/**
 * Created by CH on 2017/11/20.
 */

public class ResultBean implements Serializable{

    private Object result;
    private String memory;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }
}
