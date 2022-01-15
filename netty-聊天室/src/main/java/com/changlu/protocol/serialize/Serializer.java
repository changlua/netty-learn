package com.changlu.protocol.serialize;


/**
 * @ClassName Serializer
 * @Author ChangLu
 * @Date 2022/1/15 14:18
 * @Description 自定义序列化接口
 */
public interface Serializer {

    /**
     * 反序列化方法
     * @param clazz 反序列化的Class类型
     * @param bytes 传输过来的字节数组
     * @param <T>
     * @return 返回指定T类型的结果对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    /**
     * 序列化方法
     * @param object 序列化的对象
     * @param <T>
     * @return 序列化对象采用指定的算法来转换得到的字节数组
     */
    <T> byte[] serialize(T object);

}
