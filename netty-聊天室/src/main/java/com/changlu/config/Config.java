package com.changlu.config;

import com.changlu.protocol.serialize.Serializer;
import com.changlu.protocol.serialize.SerializerAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @ClassName Config
 * @Author ChangLu
 * @Date 2022/1/15 15:20
 * @Description 配置类
 */
public class Config {
    static Properties properties;
    public static byte serializerNum;

    static {
        try (InputStream is = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(is);
            init();
        } catch (IOException e) {
            throw new RuntimeException("读取配置文件/application.properties异常", e);
        }
    }

    private static void init() {
        serializerNum = (byte) getSerializerAlgorithm().ordinal();
    }

    /**
     * 获取配置文件中的序列化方式
     * @return 序列化算法实现枚举类
     */
    public static SerializerAlgorithm getSerializerAlgorithm(){
        String algorithmName = properties.getProperty("serialize.algorithm");
        if (algorithmName == null) {
            return SerializerAlgorithm.Java;
        } else {
            //从枚举类中根据序列化算法名字来取到枚举类算法实例
            return SerializerAlgorithm.valueOf(algorithmName);
        }
    }

}