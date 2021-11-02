package com.github.mybatis;

/**
 * Mybatis增强统一异常
 *
 * @author echils
 */
public class MybatisExpandException extends RuntimeException {

    public MybatisExpandException() {
        super();
    }

    public MybatisExpandException(String message) {
        super(message);
    }

    public MybatisExpandException(String message, Throwable cause) {
        super(message, cause);
    }

    public MybatisExpandException(Throwable cause) {
        super(cause);
    }

    protected MybatisExpandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
