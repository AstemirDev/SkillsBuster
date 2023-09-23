package ru.astemir.skillsbuster.common.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionUtils {


    public static Class<?> getGeneric(Field field, int index) {
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        Type[] actualTypeArguments = type.getActualTypeArguments();
        Type argumentType = actualTypeArguments[index];
        if (argumentType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) argumentType;
            return (Class<?>) parameterizedType.getRawType();
        } else if (argumentType instanceof Class) {
            return (Class<?>) argumentType;
        } else {
            throw new IllegalArgumentException("Unsupported argument type: " + argumentType);
        }
    }

    public static Object invokeMethod(Object instance,Method method,Object... args){
        try {
            return method.invoke(instance,args);
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz,String name,Class<?>... parameters){
        try {
            return clazz.getDeclaredMethod(name,parameters);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T extends Annotation> List<Method> getAnnotatedMethods(Class<?> clazz,Class<T> annotationClass){
        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            T annotation = method.getAnnotation(annotationClass);
            if (annotation != null){
                methods.add(method);
            }
        }
        return methods;
    }


    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> allFields = new ArrayList<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                allFields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return allFields;
    }

    public static <T> T getValue(Object instance,Field field){
        try {
            field.setAccessible(true);
            return (T)field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldValue(Object instance,Field field,Object value){
        try {
            field.setAccessible(true);
            field.set(instance,value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration, String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }

    public static <T> List<T> newList(Class<?> listType){
        if (listType == List.class){
            return new ArrayList<>();
        }else{
            return (List<T>) ReflectionUtils.newInstance(listType);
        }
    }

    public static <K,V> Map<K,V> newMap(Class<?> mapType){
        if (mapType == Map.class){
            return new HashMap<>();
        }else{
            return (Map<K,V>) ReflectionUtils.newInstance(mapType);
        }
    }

    public static <T> T newInstance(Class<T> className){
        try {
            return className.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
