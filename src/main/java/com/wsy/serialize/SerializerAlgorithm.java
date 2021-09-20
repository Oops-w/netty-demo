package com.wsy.serialize;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author wsy
 * @date 2021/9/20 9:11 上午
 * @Description
 */
@Slf4j
public enum SerializerAlgorithm implements Serializer {
    /**
     * jdk serialize
     */
    JDK() {
        @Override
        public <T> byte[] serialize(T object) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(object);
                return bos.toByteArray();
            } catch (IOException e) {
                log.error("serialize exception", e);
                throw new RuntimeException("serialize exception {}", e);
            }
        }

        @Override
        public <T> T deserialize(Class<T> clazz, byte[] bytes) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return (T) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                log.error("deserialize exception", e);
                throw new RuntimeException("deserialize exception {}", e);
            }
        }
    },
    JSON() {
        @Override
        public <T> byte[] serialize(T object) {
            try {
                byte[] bytes = gson.toJson(object).getBytes(StandardCharsets.UTF_8);
                return bytes;
            } catch (Exception e) {
                log.error("serialize exception", e);
                throw new RuntimeException("serialize exception {}", e);
            }
        }

        @Override
        public <T> T deserialize(Class<T> clazz, byte[] bytes) {
            try {
                T object = gson.fromJson(new String(bytes, StandardCharsets.UTF_8), clazz);
                return object;
            } catch (Exception e) {
                log.error("deserialize exception", e);
                throw new RuntimeException("deserialize exception {}", e);
            }
        }
    };
    static Gson gson = null;

    static {
        gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
    }

    @Slf4j
    private static class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                String json = jsonElement.getAsString();
                return Class.forName(json);
            } catch (Exception e) {
                log.error("json parse exception", e);
                throw new JsonParseException("json parse exception", e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> aClass, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(aClass.getName());
        }
    }

    @Override
    public <T> byte[] serialize(T object) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return null;
    }
}
