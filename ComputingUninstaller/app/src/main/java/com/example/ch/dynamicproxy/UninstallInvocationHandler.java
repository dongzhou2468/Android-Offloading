package com.example.ch.dynamicproxy;

import android.util.Log;
import com.example.ch.utils.SocketUtil;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CH on 2017/4/15.
 */

public class UninstallInvocationHandler implements InvocationHandler {

    private Object target;

    UninstallInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // send method's information and arguments to cloud
        String apkName = (String) target.getClass().getInterfaces()[0].getField("APKNAME").get(target);
        String packageName = (String) target.getClass().getInterfaces()[0].getField("PACKAGENAME").get(target);
        String className = target.getClass().getName();
        Log.i(SocketUtil.LOGTAG, "BEFORE invoke...... " +
                "apk: " + apkName +
                ", package: " + packageName +
                ", class: " + className +
                ", args: " + args[0].getClass().getName());

        Map<String, Object> uninstallInfo= new HashMap<String, Object>();
        uninstallInfo.put("ApkName", apkName);
        uninstallInfo.put("PackageName", packageName);
        uninstallInfo.put("ClassName", className);
        uninstallInfo.put("MethodName", method.getName());
        for (int i = 0; i < args.length; i++) {
            uninstallInfo.put("Arg" + i, args[i]);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(uninstallInfo);
        byte[] bytes = baos.toByteArray();
        Log.i(SocketUtil.LOGTAG, bytes.length + "");
        String result = SocketUtil.TCPSendAndRecv(bytes);

//        Object obj = method.invoke(target, args);
//        Log.i(SocketUtil.LOGTAG, "AFTER invoke...... " + obj.toString());
        return Integer.parseInt(result);
    }
}
