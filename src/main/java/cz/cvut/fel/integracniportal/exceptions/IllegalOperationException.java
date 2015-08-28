package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Radek Jezdik
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalOperationException extends RuntimeException {

    public IllegalOperationException(String message) {
        super(message);
    }

}
