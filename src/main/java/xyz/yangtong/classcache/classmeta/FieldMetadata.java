package xyz.yangtong.classcache.classmeta;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * created by yangtong on 2025/4/15 16:54:24
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

    public FieldMetadata(Field sourceField) {
        this.name = sourceField.getName();
        this.sourceField = sourceField;
        this.type = sourceField.getType();
        this.modifiers = sourceField.getModifiers();
        this.annotations = Arrays.stream(sourceField.getAnnotations())
                .collect(Collectors.toMap(Annotation::annotationType, a -> a));
    }

    /**
     * 获取 obj 对象的字段值（内部使用 VarHandle）
     */
    public <T> Object get(T obj) throws IllegalAccessException {
        try {
            return sourceField.get(obj);
        } catch (IllegalAccessException e) {
            throw e; // 句柄初始化阶段的访问异常
        } catch (Throwable t) {
            // VarHandle 的 get 本身不抛受检异常，这里统一转成 IllegalAccessException 以兼容原签名
            IllegalAccessException iae = new IllegalAccessException(t.getMessage());
            iae.initCause(t);
            throw iae;
        }
    }

    /**
     * 设置 obj 对象的字段值（同样使用 VarHandle）
     */
    public <T> void set(T obj, Object value) throws IllegalAccessException {
        try {
            sourceField.set(obj, value);
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
