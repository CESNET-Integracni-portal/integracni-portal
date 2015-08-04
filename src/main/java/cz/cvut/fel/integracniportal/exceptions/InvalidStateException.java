package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Radek Jezdik
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidStateException extends BaseException {

    public InvalidStateException() {
    }

    public InvalidStateException(String message) {
        super(message);
    }

    public InvalidStateException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public InvalidStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStateException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }

    public InvalidStateException(Throwable cause) {
        super(cause);
    }

    public InvalidStateException(Throwable cause, String code, Object... args) {
        super(cause, code, args);
    }

    public InvalidStateException(String code, Object... args) {
        super(code, args);
    }

}