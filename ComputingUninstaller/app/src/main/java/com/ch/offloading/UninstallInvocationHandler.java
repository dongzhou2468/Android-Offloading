package com.ch.offloading;

import android.icu.text.IDNA;
import android.util.Log;

import com.example.ch.computinguninstaller.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.jar.Manifest;

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
        // 只能将支持 java.io.Serializable 接口的对象写入流中
        // 每个serializable对象的类都被编码，编码内容包括类名和类签名、对象的字段值和数组值，以及从初始对象中引用的其他所有对象的闭包。
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(computeInfo);
        byte[] bytes = baos.toByteArray();
        Log.i(SocketUtil.LOGTAG, "length of computeInfo: " + bytes.length);

        /*
        // collection
        byte[] testBytes;
        for (int i = 0; i < 400; i++) {
            if (i % 10 < 8)
                testBytes = rePack4Col(computeInfo, i % 10);
            else
                continue;
            SocketUtil.TCPSendAndRecv(testBytes, MainActivity.Rn.get(String.valueOf(18 + i % 10)));
            Thread.sleep(6000);
        }
        */

        Object result = Offloading.TCPSendAndRecv(bytes);
//        Object result = SocketUtil.TCPSendAndRecv(bytes, MainActivity.Rn.get(args[0].toString()));
        if (result == null) {
            // do some policies
            Log.i("myLog", "result is null, execute locally");
        }

//        Object obj = method.invoke(target, args);
//        Log.i(SocketUtil.LOGTAG, "AFTER invoke...... " + obj.toString());

        return result;
    }

    private byte[] rePack4Col(InfoBean object, int i) {

        Object[] params = new Integer[2];
        params[0] = 18 + i;
        params[1] = 2;
        object.setParams(params);
        Log.i(SocketUtil.LOGTAG, object.toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
}
