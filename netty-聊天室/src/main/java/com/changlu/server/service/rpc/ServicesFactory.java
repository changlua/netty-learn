package com.changlu.server.service.rpc;

import com.google.gson.*;
import com.sun.deploy.config.Config;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ServicesFactory
 * @Author ChangLu
 * @Date 2022/1/16 22:34
 * @Description 获取service实现类工厂
 */
public class ServicesFactory {

    private static Properties properties;
    private static Map<Class<?>, Object> servicesMap = new ConcurrentHashMap<>();

    static {
        try (InputStream is = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(is);
            final Set<String> names = properties.stringPropertyNames();
            for (String name : names) {
                if (name.endsWith("Service")){
                    final Class<?> interfaceClass = Class.forName(name);
                    final Class<?> instanceClass = Class.forName(properties.getProperty(name));
                    servicesMap.put(interfaceClass, instanceClass.newInstance());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static  <T> T getServiceImpl(Class<T> clazz){
        return (T) servicesMap.get(clazz);
    }

}