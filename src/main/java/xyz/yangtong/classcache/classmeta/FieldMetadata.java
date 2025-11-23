package xyz.yangtong.classcache.classmeta;


import xyz.yangtong.classcache.ClassCacheException;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字段元数据
 */
public class FieldMetadata {
    /**
     * 字段名称
     */
    private final String name;
    /**
     * 原始字段反射对象
     */
    private final Field sourceField;
    /**
     * 注解集合
     */
    private final Map<Class<? extends Annotation>, Annotation> annotations;

    /**
     * 字段类型/修饰符，便于判断与日志
     */
    private final Class<?> type;

    /**
     * 修饰符
     */
    private final int modifiers;

    /**
     * MethodHandle 用于优化字段访问
     */
    private final MethodHandle getter;
    private final MethodHandle setter;

    public FieldMetadata(Field sourceField) {
        this.name = sourceField.getName();
        this.sourceField = sourceField;
        this.type = sourceField.getType();
        this.modifiers = sourceField.getModifiers();
        this.annotations = Arrays.stream(sourceField.getAnnotations())
                .collect(Collectors.toMap(Annotation::annotationType, a -> a));

        // 获取 MethodHandles.Lookup 实例
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        // 使用 MethodHandles 获取 getter 和 setter 方法的句柄
        try {
            getter = lookup.unreflectGetter(sourceField);
            setter = lookup.unreflectSetter(sourceField);
        } catch (IllegalAccessException e) {
            throw new ClassCacheException(e);
        }

    }

    /**
     * 获取 obj 对象的字段值
     */
    public <T> Object get(T obj) throws IllegalAccessException {
        try {
            return getter.invoke(obj);
        } catch (IllegalAccessException e) {
            throw e;
        } catch (Throwable t) {
            IllegalAccessException iae = new IllegalAccessException(t.getMessage());
            iae.initCause(t);
            throw iae;
        }
    }

    /**
     * 设置 obj 对象的字段值
     */
    public <T> void set(T obj, Object value) throws IllegalAccessException {
        try {
            setter.invoke(obj, value);
        } catch (IllegalAccessException e) {
            throw e;
        } catch (Throwable t) {
            IllegalAccessException iae = new IllegalAccessException(t.getMessage());
            iae.initCause(t);
            throw iae;
        }
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(Class<A> type) {
        return (A) annotations.get(type);
    }

    public boolean hasAnnotation(Class<? extends Annotation> type) {
        return annotations.containsKey(type);
    }

    public Type getGenericType() {
        return sourceField.getGenericType();
    }

    /* ============================ 字段修饰符 ============================ */

    public boolean isPublic() {
        return Modifier.isPublic(modifiers);
    }

    public boolean isProtected() {
        return Modifier.isProtected(modifiers);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(modifiers);
    }

    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }

    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(modifiers);
    }

    public boolean isTransient() {
        return Modifier.isTransient(modifiers);
    }

    /* ============================ getter ============================ */
    public String getName() {
        return name;
    }

    public Field getSourceField() {
        return sourceField;
    }

    public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
        return annotations;
    }

    public Class<?> getType() {
        return type;
    }

    public int getModifiers() {
        return modifiers;
    }
}
