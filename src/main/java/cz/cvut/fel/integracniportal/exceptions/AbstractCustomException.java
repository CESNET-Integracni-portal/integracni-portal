package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Abstract exception class for resolvable exception text.
 */
public abstract class AbstractCustomException extends Exception {

    MessageSourceResolvable errorObject;

    public AbstractCustomException(MessageSourceResolvable e) {
        errorObject = e;
    }

    public AbstractCustomException(String code) {
        errorObject = new DefaultMessageSourceResolvable(code);
    }

    public AbstractCustomException(String code, Object... args) {
        String[] codes = {code};
        errorObject = new DefaultMessageSourceResolvable(codes, args);
    }

    public MessageSourceResolvable getErrorObject() {
        return errorObject;
    }
}
