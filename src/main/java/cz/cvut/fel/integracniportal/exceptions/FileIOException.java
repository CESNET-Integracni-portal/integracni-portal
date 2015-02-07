package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Radek Jezdik
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class FileIOException extends BaseException {

    public FileIOException() {
    }

    public FileIOException(String message) {
        super(message);
    }

    public FileIOException(String message, String code, Object... args) {
        super(message, code, args);
    }

    public FileIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileIOException(String message, Throwable cause, String code, Object... args) {
        super(message, cause, code, args);
    }

    public FileIOException(Throwable cause) {
        super(cause);
    }

    public FileIOException(Throwable cause, String code, Object... args) {
        super(cause, code, args);
    }

    public FileIOException(String code, Object... args) {
        super(code, args);
    }
}
