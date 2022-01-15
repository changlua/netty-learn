package com.changlu.protocol.serialize;

import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName SerializeEnum
 * @Author ChangLu
 * @Date 2022/1/15 15:00
 * @Description 序列化实现类
 */
public enum SerializerAlgorithm implements Serializer{
    Java{
        @Override
        public <T> T deserialize(Class<T> clazz, byte[] bytes) {
            T obj;
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                obj = (T) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("反序列化失败", e);
            }
            return obj;
        }

        @Override
        public <T> byte[] serialize(T object) {
            byte[] bytes;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos);
            ) {
                oos.writeObject(object);
                bytes = baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("序列化失败", e);
            }
            return bytes;
        }
    },

    Json{
        @Override
        public <T> T deserialize(Class<T> clazz, byte[] bytes) {
            String json = new String(bytes, StandardCharsets.UTF_8);
            return new Gson().fromJson(json, clazz);
        }

        @Override
        public <T> byte[] serialize(T object) {
            String json = new Gson().toJson(object);
            return json.getBytes(StandardCharsets.UTF_8);
        }
    }
}