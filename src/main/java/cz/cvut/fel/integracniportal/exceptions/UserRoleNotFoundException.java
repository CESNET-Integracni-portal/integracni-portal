package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.context.MessageSourceResolvable;

public class UserRoleNotFoundException extends AbstractCustomException {

    public UserRoleNotFoundException(MessageSourceResolvable e) {
        super(e);
    }

    public UserRoleNotFoundException(String code) {
        super(code);
    }

    public UserRoleNotFoundException(String code, Object... args) {
        super(code, args);
    }
}
