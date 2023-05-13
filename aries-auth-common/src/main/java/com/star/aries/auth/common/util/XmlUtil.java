package com.star.aries.auth.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class XmlUtil {
    private static final XmlMapper xmlMapper = new XmlMapper();
    private static final XStream xStream = new XStream();

    static {
        xmlMapper.setDefaultUseWrapper(false);
        //字段为null，自动忽略，不再序列化
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //XML标签名:使用骆驼命名的属性名，
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        //设置转换模式
        xmlMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);

        XStream.setupDefaultSecurity(xStream);
    }

    public static <T> String formatXML(T entity) {
        String res = null;
        try {
            res = xmlMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static <T> T parseXML(String xml, Class<T> clazz) {
        try {
            T entity = xmlMapper.readValue(xml, clazz);
            return entity;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T readFormFile(String filePath, Class<T> clazz) {
        xStream.allowTypes(new Class[]{clazz});
        File file = new File(filePath);
        xStream.alias(clazz.getSimpleName(), clazz);
        T obj = (T) xStream.fromXML(file);
        return obj;
    }

    public static <T> void saveToFile(String filePath, T obj) {
        xStream.allowTypes(new Class[]{obj.getClass()});
        try {
            FileOutputStream fs = new FileOutputStream(filePath);
            xStream.alias(obj.getClass().getSimpleName(), obj.getClass());
            xStream.toXML(obj, fs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
