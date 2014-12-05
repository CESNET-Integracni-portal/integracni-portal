package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.context.MessageSourceResolvable;

public class PermissionNotAssignableToRoleException extends AbstractCustomException {

    public PermissionNotAssignableToRoleException(MessageSourceResolvable e) {
        super(e);
    }

    public PermissionNotAssignableToRoleException(String code) {
        super(code);
    }

    public PermissionNotAssignableToRoleException(String code, Object... args) {
        super(code, args);
    }
}
