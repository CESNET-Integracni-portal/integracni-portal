package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistsException extends BaseException {

    public AlreadyExistsException(String code) {
        super(code);
    }

    public AlreadyExistsException(String code, Object... args) {
        super(code, args);
    }

}
