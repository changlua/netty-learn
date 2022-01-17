package com.changlu.client;

import com.changlu.server.service.rpc.RpcService;
import com.changlu.server.service.rpc.RpcServiceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @ClassName Test
 * @Author ChangLu
 * @Date 2022/1/17 18:55
 * @Description TODO
 */
public class Test {

    static class OwnInvocation implements InvocationHandler{

        private Object obj;

        public OwnInvocation(Object obj){
            this.obj = obj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(this.obj,args);
        }
    }

    public static void main(String[] args) {
        final ClassLoader classLoader = RpcService.class.getClassLoader();
        final Class[] classes = {RpcService.class};
        RpcService obj = (RpcService) Proxy.newProxyInstance(classLoader, classes, new OwnInvocation((new RpcServiceImpl())));
        System.out.println(obj.sayHello("changlu"));

    }
}