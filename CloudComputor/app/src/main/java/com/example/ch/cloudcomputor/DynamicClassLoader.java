package com.example.ch.cloudcomputor;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.example.ch.dynamicproxy.InfoBean;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by CH on 2017/5/20.
 */

public class DynamicClassLoader {

    // dex(apk)文件路径
    private static String dexDir;
    private static String dexOutputDir;
    private static ClassLoader classLoader;

    private static PackageManager packageManager;

    private static Map<Class, Class> paramType = new HashMap<Class, Class>();

    public static void setDir(String appDexDir, String appDexOutputDir, ClassLoader appClassLoader, PackageManager pm) {
        dexDir = appDexDir;
        dexOutputDir = appDexOutputDir;
        classLoader = appClassLoader;

        packageManager = pm;

        paramType.put(Integer.class, Integer.TYPE);
        paramType.put(Double.class, Double.TYPE);
        paramType.put(Boolean.class, Boolean.TYPE);
        paramType.put(Byte.class, Byte.TYPE);
        paramType.put(Character.class, Character.TYPE);
        paramType.put(Float.class, Float.TYPE);
        paramType.put(Long.class, Long.TYPE);
        paramType.put(Short.class, Short.TYPE);
    }

    /** bad behavior **/
    public static Object DexClassLoaderWay(InfoBean infoBean) {

        Object result = null;
        String dexPath = dexDir + File.separator + "infoBean.getApkName()";
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, dexOutputDir, null, classLoader);
        try {
            Class dynClass = dexClassLoader.loadClass(infoBean.getClassName());
            Object obj = dynClass.newInstance();
            Object[] params = infoBean.getParams();
            Class[] reflParams = new Class[params.length];
            for (int i = 0; i < reflParams.length; i++) {
                reflParams[i] = paramType.get(params[i].getClass());
            }
            Method method = dynClass.getMethod(infoBean.getMethodName(), reflParams);
            long timeBeforeInvoke = System.currentTimeMillis();
            Log.i(SocketUtil.LOGTAG, "begin invoke method...");
            result = method.invoke(obj, params);
            // time log
            Log.i(SocketUtil.LOGTAG, "invoke method time: " + (double) (System.currentTimeMillis() - timeBeforeInvoke) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Object PathClassLoaderWay(InfoBean infoBean) {

        Object result = null;
        Intent intent = new Intent(infoBean.getActionInIntent(), null);
        List<ResolveInfo> resolveinfoes =  packageManager.queryIntentActivities(intent, 0);
        ActivityInfo actInfo = resolveinfoes.get(0).activityInfo;
        String apkPath = actInfo.applicationInfo.sourceDir;
        String libPath = actInfo.applicationInfo.nativeLibraryDir;
        PathClassLoader pathClassLoader = new PathClassLoader(apkPath, libPath, classLoader);
        try {
            Class dynClass = pathClassLoader.loadClass(infoBean.getClassName());
            Object obj = dynClass.newInstance();
            Object[] params = infoBean.getParams();
            Class[] reflParams = new Class[params.length];
            for (int i = 0; i < reflParams.length; i++) {
                reflParams[i] = paramType.get(params[i].getClass());
            }
            Method method = dynClass.getMethod(infoBean.getMethodName(), reflParams);
            long timeBeforeInvoke = System.currentTimeMillis();
            Log.i(SocketUtil.LOGTAG, "begin invoke method...");
            result = method.invoke(obj, params);
            // time log
            Log.i(SocketUtil.LOGTAG, "invoke method time: " + (double) (System.currentTimeMillis() - timeBeforeInvoke) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Object ReflectTest(InfoBean infoBean) {

        Object result = null;
        try {
            Class dynClass = Class.forName("com.example.ch.cloudcomputor.DropEgg");
            Object obj = dynClass.newInstance();
            Log.i(SocketUtil.LOGTAG, "loaded class type: " + obj.getClass());
            Object[] params = infoBean.getParams();
            Class[] reflParams = new Class[params.length];
            for (int i = 0; i < reflParams.length; i++) {
                reflParams[i] = paramType.get(params[i].getClass());
            }
            Method method = dynClass.getMethod(infoBean.getMethodName(), reflParams);
            long timeBeforeInvoke = System.currentTimeMillis();
            result = method.invoke(obj, params);
            // time log
            Log.i(SocketUtil.LOGTAG, "invoke method time: " + (double) (System.currentTimeMillis() - timeBeforeInvoke) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
