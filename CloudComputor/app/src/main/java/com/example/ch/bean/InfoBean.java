package com.example.ch.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by CH on 2017/4/20.
 */

public class InfoBean implements Serializable{

    private String apkName;
    private String packageName;
    private String className;
    private String methodName;
    private Object[] params;

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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
        return "apkName='" + apkName + '\'' +
                "\npackageName='" + packageName + '\'' +
                "\nclassName='" + className + '\'' +
                "\nmethodName='" + methodName + '\'' +
                "\nparams=" + Arrays.toString(params);
    }
}
