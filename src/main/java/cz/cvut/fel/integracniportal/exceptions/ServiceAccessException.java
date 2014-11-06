package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.context.MessageSourceResolvable;

public class ServiceAccessException extends AbstractCustomException {

    public ServiceAccessException(MessageSourceResolvable e) {
        super(e);
    }

    public ServiceAccessException(String code) {
        super(code);
    }

    public ServiceAccessException(String code, Object... args) {
        super(code, args);
    }
}
