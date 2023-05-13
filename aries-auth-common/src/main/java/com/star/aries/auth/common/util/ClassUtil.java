package com.star.aries.auth.common.util;

import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ClassUtil {

    public static Map<String, String> getFieldType(Class<?> c) {
        Map<String, String> res = Maps.newConcurrentMap();
        Field[] field = c.getDeclaredFields();
        Arrays.stream(field).forEach((p) -> {
            try {
                String name = p.getName();
                String upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
                Method m = c.getMethod("get" + upperName);
                String[] strings = m.getReturnType().getName().split("\\.");
                res.put(name, strings[strings.length - 1]);
            } catch (NoSuchMethodException var7) {
                var7.printStackTrace();
            }

        });
        return res;
    }

    public static boolean setValue(Method[] toMethods, Method fromMethod, Method method, Object from, Object to) {
        boolean res = false;
        try {
            String toMethodName = "set" + method.getName().substring(3, method.getName().length());
            Method toMethod = findMethodByName(toMethods, toMethodName);
            if (toMethod != null) {
                Object value;
                value = fromMethod.invoke(from, new Object[0]);
                if (value != null) {
                    if (value instanceof Collection) {
                        Collection newValue = (Collection) value;
                        if (newValue.size() > 0) {
                            toMethod.invoke(to, new Object[]{newValue});
                            res = true;
                        }
                    } else {
                        toMethod.invoke(to, new Object[]{value});
                        res = true;
                    }
                }
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            return res;
        }
    }

    /**
     * 从方法数组中获取指定名称的方法
     *
     * @param methods
     * @param name
     * @return
     */
    private static Method findMethodByName(Method[] methods, String name) {
        for (int j = 0; j < methods.length; j++) {
            if (methods[j].getName().equals(name)) {
                return methods[j];
            }
        }
        return null;
    }

    /**
     * 获取对象的属性以及对应的值
     *
     * @param model
     * @return
     */
    public static Map<String, Object> get(Object model) {
        Map<String, Object> res = Maps.newConcurrentMap();
        // 获取实体类的所有属性，返回Field数组
        Field[] field = model.getClass().getDeclaredFields();
        Arrays.stream(field).forEach(p -> {
            try {
                String name = p.getName();
                // 将属性的首字符大写，方便构造get，set方法
                String upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
                Method m = model.getClass().getMethod("get" + upperName);
                Object value = m.invoke(model, new Object[0]);
                if (value != null) {
                    res.put(name, value);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        return res;
    }
}
