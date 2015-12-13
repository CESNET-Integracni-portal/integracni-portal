package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Eldar Iosip
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NodeNotFoundException extends BaseException {

    public NodeNotFoundException(String message) {
        super(message);
    }

    public NodeNotFoundException(String code, Object... args) {
        super(code, args);
    }

}
