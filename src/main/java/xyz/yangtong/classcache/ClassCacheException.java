package xyz.yangtong.classcache;

/**
 * 异常类
 */
public class ClassCacheException extends RuntimeException {
    private final String message;

    public ClassCacheException(String msg) {
        super(msg);
        this.message = msg;
    }

    public ClassCacheException(String msg, Throwable cause) {
        super(msg, cause);
        this.message = msg;
    }

    public ClassCacheException(Throwable cause) {
        super(cause.getMessage(), cause);
        this.message = cause.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
