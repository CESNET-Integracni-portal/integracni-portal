package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends BaseException {

    public AccessDeniedException(String code) {
        super(code);
    }

    public AccessDeniedException(String code, Object... args) {
        super(code, args);
    }

}
