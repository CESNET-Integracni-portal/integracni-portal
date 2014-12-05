package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.context.MessageSourceResolvable;

public class PermissionNotFoundException extends AbstractCustomException {

    public PermissionNotFoundException(MessageSourceResolvable e) {
        super(e);
    }

    public PermissionNotFoundException(String code) {
        super(code);
    }

    public PermissionNotFoundException(String code, Object... args) {
        super(code, args);
    }
}
