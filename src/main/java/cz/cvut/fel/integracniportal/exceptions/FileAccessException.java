package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class FileAccessException extends BaseException {

    public FileAccessException() {
    }

    public FileAccessException(String message) {
        super(message);
    }

    public FileAccessException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public FileAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileAccessException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }

    public FileAccessException(Throwable cause) {
        super(cause);
    }

    public FileAccessException(Throwable cause, String code, Object... args) {
        super(cause, code, args);
    }

    public FileAccessException(String code, Object... args) {
        super(code, args);
    }

}
