package xyz.yangtong.classcache.invoke.supprt;


import xyz.yangtong.classcache.ClassCache;
import xyz.yangtong.classcache.classmeta.MethodMetadata;
import xyz.yangtong.classcache.invoke.Invoker;
import xyz.yangtong.classcache.invoke.Reflects;

import java.lang.reflect.InvocationTargetException;

/**
 * 基于反射实现的调用
 */
public class ReflectInvoke implements Invoker {
    @Override
    public <T> Object getFieldValue(T target, String fieldName) throws InvocationTargetException, IllegalAccessException {
        //通过getter方法获取字段的值
        String getterStr = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return invokeMethod(target, getterStr, new Class<?>[0]);
    }

    @Override
    public <T> void setFieldValue(T target, String fieldName, Object value) throws InvocationTargetException, IllegalAccessException {
        //获取setter方法名称
        String setterStr = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        //获取方法的参数类型数组
        Class<?>[] types = Reflects.argsToClasses(value);

        invokeMethod(target, setterStr, types, value);
    }

    @Override
    public <T> Object invokeMethod(T target, String methodName, Class<?>[] paramTypes, Object... args) throws InvocationTargetException, IllegalAccessException {
        MethodMetadata method = ClassCache.getMethod(target.getClass(), methodName, paramTypes);
        if (!method.isPublic()) {
            throw new ClassCastException("无访问权限");
        }
        return method.invoke(target, args);
    }
}
