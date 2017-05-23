package com.example.ch.dynamicproxy;

import android.util.Log;

import com.example.ch.bean.InfoBean;
import com.example.ch.utils.SocketUtil;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by CH on 2017/4/15.
 */

public class UninstallInvocationHandler implements InvocationHandler {

    private Object target;
    private static final String ACTIONININTENT = "com.ch.androidoffloading.client";

    UninstallInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // send uninstalled's info to cloud
//        String apkName = (String) target.getClass().getInterfaces()[0].getField("APKNAME").get(target);
//        String packageName = (String) target.getClass().getInterfaces()[0].getField("PACKAGENAME").get(target);
        String actionInIntent = ACTIONININTENT;
        String className = target.getClass().getName();
        String methodName = method.getName();
        Log.i(SocketUtil.LOGTAG, "BEFORE invoke...... " +
//                "apk: " + apkName +
//                ", package: " + packageName +
                ", class: " + className +
                ", method: " + methodName +
                ", args: " + args.toString() +
                ", args[0] : " + args[0].getClass().getName() +
                ", args[1] : " + args[1].getClass().getName());

        InfoBean computeInfo = new InfoBean();
//        computeInfo.setApkName(apkName);
//        computeInfo.setPackageName(packageName);
        computeInfo.setActionInIntent(actionInIntent);
        computeInfo.setClassName(className);
        computeInfo.setMethodName(methodName);
        computeInfo.setParams(args);
        Log.i(SocketUtil.LOGTAG, computeInfo.toString());

        /*Map<String, Object> uninstallInfo= new HashMap<String, Object>();
        uninstallInfo.put("ApkName", apkName);
        uninstallInfo.put("PackageName", packageName);
        uninstallInfo.put("ClassName", className);
        uninstallInfo.put("MethodName", method.getName());
        for (int i = 0; i < args.length; i++) {
            uninstallInfo.put("Arg" + i, args[i]);
        }*/

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(computeInfo);
        byte[] bytes = baos.toByteArray();
        Log.i(SocketUtil.LOGTAG, "length of computeInfo: " + bytes.length);
        Object result = SocketUtil.TCPSendAndRecv(bytes);
        if (result == null) {
            // do some policies
        }

//        Object obj = method.invoke(target, args);
//        Log.i(SocketUtil.LOGTAG, "AFTER invoke...... " + obj.toString());

        return result;
    }
}
