package com.rsostream.tcp.util;

public class RabbitMQException extends Exception {
    private static final long serialVersionUID = 6765369561906450023L;

    public RabbitMQException() {
        super();
    }

    /**
     * @param message the message for this exception
     */
    public RabbitMQException(String message) {
        super(message);
    }

    /**
     * @param cause the root cause
     */
    public RabbitMQException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message the message for this exception
     * @param cause   the root cause
     */
    public RabbitMQException(String message, Throwable cause) {
        super(message, cause);
    }
}
