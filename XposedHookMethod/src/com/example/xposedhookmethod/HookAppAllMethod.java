package com.example.xposedhookmethod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookAppAllMethod {

	public static Set<String> methodStringSet = Collections.synchronizedSet(new HashSet<String>());
	public static Set<String> methodSignSet = Collections.synchronizedSet(new HashSet<String>());
	public static Set<String> callMethodSignSet = Collections.synchronizedSet(new HashSet<String>()) ;
	
//	public static final String FILTER_PKGNAME = "com.example.howold";
	public static final String FILTER_PKGNAME = "cn.facecore.facecoredemo";
//	public final static String FILTER_PKGNAME = "com.example.ch.computinguninstaller";
//	public static final String FILTER_PKGNAME = "com.cnvcs.xiangqi";
	

	public static void hookMethod(LoadPackageParam loadPackageParam){
		
		String pkgname = loadPackageParam.packageName;
		if(FILTER_PKGNAME.equals(pkgname)){
			XposedBridge.log(loadPackageParam.packageName);

			//������Ϊ�˽��app��dex����hook�����⣬XposedĬ����hook��dex
			XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					ClassLoader cl = ((Context)param.args[0]).getClassLoader();
					Class<?> hookclass = null;
					try {
						hookclass = cl.loadClass("dalvik.system.DexFile");
					} catch (Exception e) {
						return;
					}
/*
					XposedHelpers.findAndHookMethod(hookclass, "loadClass", String.class, ClassLoader.class, new XC_MethodHook(){
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							XposedBridge.log("XposedHookMethod---loadClass");
							hookClassInfo((String)param.args[0], (ClassLoader)param.args[1]);
							super.beforeHookedMethod(param);
						}
					});

					XposedHelpers.findAndHookMethod(hookclass, "loadClassBinaryName", String.class, ClassLoader.class, List.class,new XC_MethodHook(){
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//							XposedBridge.log("XposedHookMethod---loadClassBinaryName");
							hookClassInfo((String)param.args[0], (ClassLoader)param.args[1]);
							super.beforeHookedMethod(param);
						}
					});

					XposedHelpers.findAndHookMethod(hookclass, "defineClass", String.class, ClassLoader.class, long.class, List.class,new XC_MethodHook(){
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							XposedBridge.log("XposedHookMethod---defineClass");
							hookClassInfo((String)param.args[0], (ClassLoader)param.args[1]);
							super.beforeHookedMethod(param);
						}
					});
*/
					/**
					 * native method, after which a class is return but not loaded yet!
					 * 5 times invoked and a class is return in last time
					 */
					XposedHelpers.findAndHookMethod(hookclass, "defineClassNative", String.class, ClassLoader.class, long.class, new XC_MethodHook(){
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							hookClassInfoSec((String)param.args[0], (ClassLoader)param.args[1], (Class) param.getResult());
							super.beforeHookedMethod(param);
						}
					});
					
				}
			});
		}
	}

	/**
	 * ��ȡdex·��
	 * @param classLoader
	 * @return
	 */
	public static String getDexPath(ClassLoader classLoader){
		try{
			Field field = classLoader.getClass().getSuperclass().getDeclaredField("pathList");
			field.setAccessible(true);
			Object objPathList = field.get(classLoader);
			Field elementsField = objPathList.getClass().getDeclaredField("dexElements");
			elementsField.setAccessible(true);
			Object[] elements =(Object[])elementsField.get(objPathList);
			for(Object obj : elements){
				Field fileF = obj.getClass().getDeclaredField("file");
				fileF.setAccessible(true);
				File file = (File)fileF.get(obj);
				return file.getAbsolutePath();
			}
		}catch(Exception e){
		}
		return null;
	}

	private static void hookClassInfo(String className, ClassLoader classLoader){
		
		//����ϵͳ����ǰ׺
		if(TextUtils.isEmpty(className)){
			return;
		}
		if(className.startsWith("android.")){
			return;
		}
		if(className.startsWith("java.")){
			return;
		}
		if(className.startsWith("com.android.")){
			return;
		}
		if(className.startsWith("com.baidu.")){
			return;
		}
		XposedBridge.log("hookClassInfo---className: " + className);
		if (!methodStringSet.contains(className)) {
			methodStringSet.add(className);
			Log.i("jw", "hookClassInfo---className: " + className);		// useless, https://github.com/rovo89/Xposed/issues/246? "storeResult()" is OK
//			XposedBridge.log("hookClassInfo---classLoader: " + classLoader);
			try{
				XposedBridge.log("hookClassInfo---writing loaded class, methodStringSet: " + methodStringSet);
				FileWriter fw = new FileWriter("/data/system/" 
						+ FILTER_PKGNAME +"_LoadedClass.txt", true);	// useless, https://github.com/rovo89/XposedBridge/issues/168
				fw.write(className);
				fw.close();
			}catch(Exception e){
				XposedBridge.log(e.toString());
			}
		}
/*
		//���÷����ȡһ��������з���
		try{
			Class<?> clazz = classLoader.loadClass(className);		// JNI ERROR local reference table overflow, ���޵ݹ飿
			//�����ȡ������з����������޷���ȡ����ķ�������������û��Ҫ��ϵ����ķ���
			//���Ҫ���ģ���ô��Ҫ����getMethods��������
			Method[] allMethods = clazz.getDeclaredMethods();
			for(Method method : allMethods){
				Class<?>[] paramTypes = method.getParameterTypes();
				String methodName = method.getName();
				Log.i("jw", methodName);
				Object[] param = new Object[paramTypes.length+1];
				for(int i=0;i<paramTypes.length;i++){
					param[i] = paramTypes[i];
				}
				
				String signStr = getMethodSign(method);
				if(TextUtils.isEmpty(signStr) || isFilterMethod(signStr)){
					continue;
				}
				
				//��ʼ����Hook�ķ�����Ϣ
				param[paramTypes.length] = new XC_MethodHook(){
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						String methodSign = getMethodSign(param);
						if(!TextUtils.isEmpty(methodSign) && !callMethodSignSet.contains(methodSign)){
							//������Ϊ���ӡ��־�����Ի����app��ANR���
							Log.i("jw", "call-->"+methodSign);
							//���ﻹ���԰ѷ����Ĳ���ֵ��ӡ�������������Ӧ�ù�����������ANR
							for(int i=0;i<param.args.length;i++){
								Log.i("jw", "==>arg"+i+":"+param.args[i]);
							}
							callMethodSignSet.add(methodSign);
						}
						super.afterHookedMethod(param);
					}
				};
				
				//��ʼ����Hook������ע��������һ�����⣬���һ��Hook�ķ��������࣬�����OOM�Ĵ��������Xposed���ߵ�����
				if(!TextUtils.isEmpty(signStr) && !methodSignSet.contains(signStr)){
					//������Ϊ���ӡ��־�����Ի����app��ANR���
					Log.i("jw", "all-->"+signStr);
					methodSignSet.add(signStr);
					XposedHelpers.findAndHookMethod(className, classLoader, methodName, param);
				}

			}
		}catch(Exception e){
			XposedBridge.log(e.toString());
		}
*/
	}

private static void hookClassInfoSec(String className, ClassLoader classLoader, Class clazz){
		
		//����ϵͳ����ǰ׺
		if(TextUtils.isEmpty(className)){
			return;
		}
		/*
		if(className.startsWith("android.")){
			return;
		}
		if(className.startsWith("java.")){
			return;
		}
		if(className.startsWith("com.android.")){
			return;
		}
		// Ad
		if(className.startsWith("com.baidu.")){
			return;
		}
		*/
		if(className.startsWith("com/android")){
			return;
		}
		if (clazz == null) {
			return;
		}
//		XposedBridge.log("hookClassInfoSec---className: " + className + ", classLoader: " + classLoader);
//		XposedBridge.log("hookClassInfoSec---Class Object: " + clazz);
		
		//���÷����ȡһ��������з���
		try{
//			Class<?> clazz = classLoader.loadClass(className);		// JNI ERROR local reference table overflow, ���޵ݹ飿
			//�����ȡ������з����������޷���ȡ����ķ�������������û��Ҫ��ϵ����ķ���
			//���Ҫ���ģ���ô��Ҫ����getMethods��������
			Method[] allMethods = clazz.getDeclaredMethods();
			for(Method method : allMethods){
				Class<?>[] paramTypes = method.getParameterTypes();
				String methodName = method.getName();
				Object[] param = new Object[paramTypes.length+1];
				for(int i=0;i<paramTypes.length;i++){
					param[i] = paramTypes[i];
				}
				String signStr = getMethodSign(method);
				if(TextUtils.isEmpty(signStr) || isFilterMethod(signStr)){
					continue;
				}
//				XposedBridge.log(signStr);

				//��ʼ����Hook�ķ�����Ϣ
				param[paramTypes.length] = new XC_MethodHook(){
					long time;
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						/*
						String methodSign = getMethodSign(param);
						if (!TextUtils.isEmpty(methodSign)) {
							XposedBridge.log("---" + methodSign + "---BeforeHookedMethod");
						}
						*/
						time = System.currentTimeMillis();
						super.beforeHookedMethod(param);
					}
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						long dura = System.currentTimeMillis() - time;
						if (dura > 40) {
							// better way to get method sign?
							String methodSign = getMethodSign(param);
							if (!TextUtils.isEmpty(methodSign)) {
								XposedBridge.log(dura + "---" + methodSign + "---AfterHookedMethod");
								storeResult(dura + "---" + methodSign);
							}
						}
						
						/*
						if(!TextUtils.isEmpty(methodSign) && !callMethodSignSet.contains(methodSign)){
							//������Ϊ���ӡ��־�����Ի����app��ANR���
							Log.i("jw", "call-->"+methodSign);
							//���ﻹ���԰ѷ����Ĳ���ֵ��ӡ�������������Ӧ�ù�����������ANR
							for(int i=0;i<param.args.length;i++){
								Log.i("jw", "==>arg"+i+":"+param.args[i]);
							}
							callMethodSignSet.add(methodSign);
						}
						*/
						super.afterHookedMethod(param);
					}
				};
				
				//��ʼ����Hook������ע��������һ�����⣬���һ��Hook�ķ��������࣬�����OOM�Ĵ��������Xposed���ߵ�����
				if(!TextUtils.isEmpty(signStr) && !methodSignSet.contains(signStr)){
					//������Ϊ���ӡ��־�����Ի����app��ANR���
//					Log.i("jw", "all-->"+signStr);
					methodSignSet.add(signStr);
					XposedHelpers.findAndHookMethod(className, classLoader, methodName, param);
				}

			}
		}catch(Exception e){
			XposedBridge.log(e.toString());
		}

	}

	/**
	 * ��ȡ������ǩ����Ϣ
	 * @param param
	 * @return
	 */
	private static String getMethodSign(MethodHookParam param){
		try{
			StringBuilder methodSign = new StringBuilder();
			methodSign.append(Modifier.toString(param.method.getModifiers())+" ");
			Object result = param.getResult();
			if(result == null){
				methodSign.append("void ");
			}else{
				methodSign.append(result.getClass().getCanonicalName() + " ");
			}
			methodSign.append(param.method.getDeclaringClass().getCanonicalName()+"."+param.method.getName()+"(");
			for(int i=0;i<param.args.length;i++){
				//������һ�����⣺��������Ĳ���ֵΪnull,��ô����ͻᱨ��! ������취��λ�ȡ���������ͣ�
				if(param.args[i] == null){
					methodSign.append("?");
				}else{
					methodSign.append(param.args[i].getClass().getCanonicalName());
				}
				if(i<param.args.length-1){
					methodSign.append(",");
				}
			}
			methodSign.append(")");
			return methodSign.toString();
		}catch(Exception e){
			return null;
		}
	}

	/**
	 * ��ȡ������ǩ����Ϣ
	 * public final native String xxx(java.lang.String,int) ��������������
	 * @param method
	 * @return
	 */
	private static String getMethodSign(Method method){
		try{
			//�����������Ǽ̳и���ķ�����Ҳ��Ҫ������
			String methodClass = method.getDeclaringClass().getCanonicalName();
//			if(methodClass.startsWith("android.") || methodClass.startsWith("java.")){
//				return null;
//			}
			StringBuilder methodSign = new StringBuilder();
			Class<?>[] paramTypes = method.getParameterTypes();
			Class<?> returnTypes = method.getReturnType();
			methodSign.append(Modifier.toString(method.getModifiers()) + " ");
			methodSign.append(returnTypes.getCanonicalName() + " ");
			methodSign.append(methodClass+"."+method.getName()+"(");
			for(int i=0;i<paramTypes.length;i++){
				methodSign.append(paramTypes[i].getCanonicalName());
				if(i<paramTypes.length-1){
					methodSign.append(",");
				}
			}
			methodSign.append(")");
			return methodSign.toString();
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * ����Object�����Դ��ļ�����������Щ�������Բ���������
	 * @param methodSign
	 * @return
	 */
	private static boolean isFilterMethod(String methodSign){
		if("public final void java.lang.Object.wait()".equals(methodSign)){
			return true;
		}
		if("public final void java.lang.Object.wait(long,int)".equals(methodSign)){
			return true;
		}
		if("public final native java.lang.Object.wait(long)".equals(methodSign)){
			return true;
		}
		if("public boolean java.lang.Object.equals(java.lang.Object)".equals(methodSign)){
			return true;
		}
		if("public java.lang.String java.lang.Object.toString()".equals(methodSign)){
			return true;
		}
		if("public native int java.lang.Object.hashCode()".equals(methodSign)){
			return true;
		}
		if("public final native java.lang.Class java.lang.Object.getClass()".equals(methodSign)){
			return true;
		}
		if("public final native void java.lang.Object.notify()".equals(methodSign)){
			return true;
		}
		if("public final native void java.lang.Object.notifyAll()".equals(methodSign)){
			return true;
		}
		// newly added exclusion
		if(methodSign.contains("access$")){
			return true;
		}
		if(methodSign.contains("abstract")){
			return true;
		}
		if(methodSign.contains("OnTimer") || methodSign.contains("handleMessage") 
//				|| methodSign.contains("readyToDraw") || methodSign.contains("onDrawFrame")
//				|| methodSign.contains("OnUpdate") || methodSign.contains("swap")
				){
			return true;
		}
		if(methodSign.contains("void")){
			return true;
		}
		return false;
	}

	public static void storeResult(String str) {
		
		FileWriter fw = null;
		try{
			fw = new FileWriter("/data/system/"
					+ FILTER_PKGNAME +"_LoadedClass.txt", true);
			XposedBridge.log("writing result...");
			fw.write(str);
			fw.write("\n");
		}catch(Exception e){
			XposedBridge.log(e.toString());
		}finally{
			try{
				if(fw != null){
					fw.close();
				}
			}catch(Exception e){
			}
		}
	}
	
	/**
	 * ����̶�ȡ���ݣ�����ʾʧ�ܵģ������������Ч�ģ���ΪmethodSignSet���ݿ��ܿ���̶�ȡʧ��
	 * @return
	 */
	@SuppressLint("SdCardPath")
	public static boolean dumpAllMethodInfo(){
		Log.i("jw", "all method size:"+methodSignSet.size());
		if(methodSignSet.size() == 0){
			return false;
		}
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fw = new FileWriter("/sdcard/"+FILTER_PKGNAME+"_allmethod.txt");
			bw = new BufferedWriter(fw);
			for(String methodStr : methodSignSet){
				bw.write(methodStr);
				bw.newLine();
			}
			return true;
		}catch(Exception e){
			Log.i("jw", "dump all method error:"+Log.getStackTraceString(e));
			return false;
		}finally{
			try{
				if(fw != null){
					fw.close();
				}
				if(bw != null){
					bw.close();
				}
			}catch(Exception e){
			}
		}
	}
	
	/**
	 * ����̶�ȡ����ʧ��
	 * @return
	 */
	@SuppressLint("SdCardPath")
	public static boolean dumpCallMethodInfo(){
		Log.i("jw", "call method size:"+callMethodSignSet.size());
		if(callMethodSignSet.size() == 0){
			return false;
		}
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fw = new FileWriter("/sdcard/"+FILTER_PKGNAME+"_callmethod.txt");
			bw = new BufferedWriter(fw);
			for(String methodStr : callMethodSignSet){
				bw.write(methodStr);
				bw.newLine();
			}
			return true;
		}catch(Exception e){
			Log.i("jw", "dump call method error:"+Log.getStackTraceString(e));
			return false;
		}finally{
			try{
				if(fw != null){
					fw.close();
				}
				if(bw != null){
					bw.close();
				}
			}catch(Exception e){
			}
		}
	}

}

