package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Eldar Iosip
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GroupNotFoundException extends BaseException {

    public GroupNotFoundException(String message) {
        super(message);
    }

    public GroupNotFoundException(String code, Object... args) {
        super(code, args);
    }

}
