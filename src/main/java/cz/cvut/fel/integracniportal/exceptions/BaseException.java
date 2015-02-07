package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Abstract exception class for resolvable exception text.
 */
public abstract class BaseException extends RuntimeException {

    private MessageSourceResolvable errorObject;

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, String code, Object... args) {
        super(message);
        initErrorObject(code, args);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(String message, Throwable cause, String code, Object... args) {
        super(message, cause);
        initErrorObject(code, args);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(Throwable cause, String code, Object... args) {
        super(cause);
        initErrorObject(code, args);
    }

    public BaseException(String code, Object... args) {
        initErrorObject(code, args);
    }

    private void initErrorObject(String code, Object[] args) {
        String[] codes = {code};
        errorObject = new DefaultMessageSourceResolvable(codes, args);
    }

    public MessageSourceResolvable getErrorObject() {
        return errorObject;
    }

}
