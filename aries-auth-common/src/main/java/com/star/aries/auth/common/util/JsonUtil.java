package com.star.aries.auth.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;


public class JsonUtil {

    public static final ObjectMapper mapper = new ObjectMapper();
    public static final String UTF_8 = "UTF-8";

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,true);
    }

    public static <T> String toString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            try {
                return mapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static <T> T toBean(String json, Class<T> tClass) {
        try {
            return mapper.readValue(json, tClass);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <E> List<E> toList(String json, Class<E> eClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, eClass));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> kClass, Class<V> vClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructMapType(Map.class, kClass, vClass));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T nativeRead(String json, TypeReference<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> byte[] objToByteArray(T obj) {
        try {
            return mapper.writeValueAsString(obj).getBytes(Charset.forName(UTF_8));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T byteArrayToObj(byte[] data, Class<T> clazz) {
        try {
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T readFromFile(String filePath, Class<T> clazz) {
        //ClassPathResource resource = new ClassPathResource("adConfig.json");
        //File file = resource.getFile();
        try {
            File file = new File(filePath);
            String jsonStr = FileUtils.readFileToString(file, "UTF-8");
            return toBean(jsonStr, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T readFromFile(File file, Class<T> clazz) {
        try {
            String jsonStr = FileUtils.readFileToString(file, "UTF-8");
            return toBean(jsonStr, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void saveToFile(String filePath, T obj) {
        try {
            File file = new File(filePath);
            String jsonStr = toString(obj);
            FileUtils.writeStringToFile(file, jsonStr, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> void saveToFile(File file, T obj) {
        try {
            String jsonStr = toString(obj);
            FileUtils.writeStringToFile(file, jsonStr, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
