package xyz.yangtong.classcache.invoke;

import java.lang.reflect.InvocationTargetException;

/**
 * 调用器
 */
public interface Invoker {
    /**
     * 获取对象的字段值
     */
    <T> Object getFieldValue(T target, String fieldName) throws IllegalAccessException, InvocationTargetException;

    /**
     * 设置对象的字段值
     */
    <T> void setFieldValue(T target, String fieldName, Object value) throws InvocationTargetException, IllegalAccessException;

    /**
     * 调用对象的方法
     */
    <T> Object invokeMethod(T target, String methodName, Class<?>[] paramTypes, Object... args) throws InvocationTargetException, IllegalAccessException;
}
