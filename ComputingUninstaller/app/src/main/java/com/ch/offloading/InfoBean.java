package com.ch.offloading;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by CH on 2017/4/20.
 */

public class InfoBean implements Serializable{

//    private String apkName;
//    private String packageName;
    // PathClassLoader does not need name of apk and package but action in intent
    private String actionInIntent;
    private String className;
    private String methodName;
    private Object[] params;

    public String getActionInIntent() {
        return actionInIntent;
    }

    public void setActionInIntent(String actionInIntent) {
        this.actionInIntent = actionInIntent;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "InfoBean{" +
                "actionInIntent='" + actionInIntent + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}
