package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PolicyNotFoundException extends BaseException {

    public PolicyNotFoundException(String code) {
        super(code);
    }

    public PolicyNotFoundException(String code, Object... args) {
        super(code, args);
    }

}
