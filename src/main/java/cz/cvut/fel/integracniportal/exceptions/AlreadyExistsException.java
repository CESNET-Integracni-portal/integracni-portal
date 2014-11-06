package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.context.MessageSourceResolvable;

public class AlreadyExistsException extends AbstractCustomException {

    public AlreadyExistsException(MessageSourceResolvable e) {
        super(e);
    }

    public AlreadyExistsException(String code) {
        super(code);
    }

    public AlreadyExistsException(String code, Object... args) {
        super(code, args);
    }
}
