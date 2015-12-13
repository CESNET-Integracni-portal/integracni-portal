package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Eldar Iosip
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AclDeniedAccessException extends BaseException {

    public AclDeniedAccessException(String code) {
        super(code);
    }

    public AclDeniedAccessException(String code, Object... args) {
        super(code, args);
    }

}
