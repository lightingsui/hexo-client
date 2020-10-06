package com.lightingsui.linuxwatcher.exception;

/**
 * 数据库异常
 *
 * @author ：隋亮亮
 * @since ：2020/10/6 17:18
 */
public class DatabaseException extends RuntimeException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public DatabaseException(String message) {
        super(message);
    }
}
