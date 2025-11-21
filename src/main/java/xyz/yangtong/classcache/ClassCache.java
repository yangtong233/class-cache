package xyz.yangtong.classcache;


import xyz.yangtong.classcache.classmeta.ClassMetadata;
import xyz.yangtong.classcache.classmeta.ConstructorMetadata;
import xyz.yangtong.classcache.classmeta.FieldMetadata;
import xyz.yangtong.classcache.classmeta.MethodMetadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类元信息缓存
 */
public class ClassCache {

    private static final Map<String, ClassMetadata<?>> cache = new ConcurrentHashMap<>();

    /**
     * 添加缓存
     */
    public static <T> ClassMetadata<T> put(Class<T> type) {
        ClassMetadata<T> classMetadata = new ClassMetadata<>(type);
        cache.put(type.getName(), classMetadata);

        return classMetadata;
    }

    /**
     * 获取类
     */
    @SuppressWarnings("unchecked")
    public static <T> ClassMetadata<T> get(Class<T> type) {
        String className = type.getName();
        if (type.getClassLoader() == null
                || className.startsWith("java.")
                || className.startsWith("javax.")
                || className.startsWith("jdk.")
                || className.startsWith("sun.")
                || className.startsWith("com.sun.")) {
            throw new IllegalArgumentException("不能缓存平台类型: " + className);
        }

        ClassMetadata<T> classMetadata = (ClassMetadata<T>) cache.get(className);
        if (classMetadata == null) {
            classMetadata = put(type);
        }
        return classMetadata;
    }

    /**
     * 获取字段
     */
    @SuppressWarnings("unchecked")
    public static <T> FieldMetadata getField(Class<T> type, String fieldName) {
        String className = type.getName();
        ClassMetadata<T> classMetadata = (ClassMetadata<T>) cache.get(className);
        if (classMetadata == null) {
            classMetadata = put(type);
        }
        return classMetadata.getField(fieldName);
    }

    /**
     * 获取构造器
     */
    @SuppressWarnings("unchecked")
    public static <T> ConstructorMetadata<T> getConstructor(Class<T> type, Class<?>... paramTypes) {
        String className = type.getName();
        ClassMetadata<T> classMetadata = (ClassMetadata<T>) cache.get(className);
        if (classMetadata == null) {
            classMetadata = put(type);
        }
        return classMetadata.getConstructor(paramTypes);
    }

    /**
     * 获取方法
     */
    @SuppressWarnings("unchecked")
    public static <T> MethodMetadata getMethod(Class<T> type, String methodName, Class<?>... paramTypes) {
        String className = type.getName();
        ClassMetadata<T> classMetadata = (ClassMetadata<T>) cache.get(className);
        if (classMetadata == null) {
            classMetadata = put(type);
        }
        return classMetadata.getMethod(methodName, paramTypes);
    }

}
