package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PermissionNotFoundException extends BaseException {

    public PermissionNotFoundException(String code) {
        super(code);
    }

    public PermissionNotFoundException(String code, Object... args) {
        super(code, args);
    }

}
