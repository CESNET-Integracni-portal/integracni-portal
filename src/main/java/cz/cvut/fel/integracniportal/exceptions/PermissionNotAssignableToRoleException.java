package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PermissionNotAssignableToRoleException extends BaseException {

    public PermissionNotAssignableToRoleException(String code) {
        super(code);
    }

    public PermissionNotAssignableToRoleException(String code, Object... args) {
        super(code, args);
    }

}
