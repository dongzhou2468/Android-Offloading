package com.ch.offloading;

import java.lang.reflect.Proxy;

/**
 * Created by CH on 2017/4/16.
 */

public class UninstallProxy {

    public static Object getProxy(Object object) {

        // 网络与卸载条件判断
        boolean networkConn = true;

        if (!networkConn) {
            return object;
        }
        // 网络类型，时间与能耗


        // 需要代理的接口，被代理类实现的多个接口都必须在这里定义
        Class[] proxyInstance = object.getClass().getInterfaces();
        // 构建AOP的Advice，这里需要传入业务类的实例
        UninstallInvocationHandler handler = new UninstallInvocationHandler(object);
        // 生成代理类的字节码加载器
        ClassLoader classLoader = UninstallProxy.class.getClassLoader();
        // 织入器，织入代码并生成代理类
        Object proxy = Proxy.newProxyInstance(classLoader, proxyInstance, handler);
        return proxy;
    }


}
