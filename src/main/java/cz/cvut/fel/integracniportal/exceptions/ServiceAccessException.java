package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceAccessException extends BaseException {

    public ServiceAccessException(String code) {
        super(code);
    }

    public ServiceAccessException(String code, Throwable e) {
        super(code, e);
    }

}
