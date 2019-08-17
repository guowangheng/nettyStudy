package com.easydo.server.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicServerInvoker<T> implements InvocationHandler {

    private Object object;

    public DynamicServerInvoker(Object object) {
        this.object = object;
    }

    public T getProxy(Class<T> classType) {
        Class<?>[] interfaces;
        if (!classType.isInterface()) {
            interfaces = classType.getInterfaces();
        } else {
            interfaces = new Class<?>[]{classType};
        }
        ClassLoader classLoader = classType.getClassLoader();
        T t = (T) Proxy.newProxyInstance(classLoader, interfaces, this);
        return t;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object object = method.invoke(this.object, args);
        return object;
    }

}
