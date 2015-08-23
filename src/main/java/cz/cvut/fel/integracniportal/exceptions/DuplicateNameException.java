package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Radek Jezdik
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateNameException extends RuntimeException {

    public DuplicateNameException() {
    }

    public DuplicateNameException(String message) {
        super(message);
    }

}
