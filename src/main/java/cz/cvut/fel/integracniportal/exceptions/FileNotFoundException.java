package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Radek Jezdik
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundException extends BaseException {

    public FileNotFoundException() {
    }

    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotFoundException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }

    public FileNotFoundException(Throwable cause) {
        super(cause);
    }

    public FileNotFoundException(Throwable cause, String code, Object... args) {
        super(cause, code, args);
    }

    public FileNotFoundException(String code, Object... args) {
        super(code, args);
    }

}
