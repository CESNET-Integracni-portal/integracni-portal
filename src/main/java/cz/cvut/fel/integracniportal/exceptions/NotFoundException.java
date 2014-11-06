package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.context.MessageSourceResolvable;

public class NotFoundException extends AbstractCustomException {

    public NotFoundException(MessageSourceResolvable e) {
        super(e);
    }

    public NotFoundException(String code) {
        super(code);
    }

    public NotFoundException(String code, Object... args) {
        super(code, args);
    }
}
