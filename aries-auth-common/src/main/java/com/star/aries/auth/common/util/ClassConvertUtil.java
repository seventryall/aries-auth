package com.star.aries.auth.common.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ClassConvertUtil {

    public static <T> T convert(Object value, Class cls) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String jsonString = mapper.writeValueAsString(value);
            return mapper.readValue(jsonString, (Class<T>) cls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> convertToList(List list, Class cls) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String jsonString = mapper.writeValueAsString(list);
            //jackson的转List方式
            CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(list.getClass(), cls);
            return mapper.readValue(jsonString, collectionType);
            //fastjson的方式
            //            return JSON.parseArray(jsonString, cls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
