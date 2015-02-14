package cz.cvut.fel.integracniportal.mvc;

import cz.cvut.fel.integracniportal.exceptions.BaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Locale;

/**
 * @author Radek Jezdik
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    @Autowired
    protected MessageSource messageSource;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity baseErrorHandler(BaseException e) {
        MessageSourceResolvable resolvable = e.getErrorObject();
        ResponseStatus an = e.getClass().getAnnotation(ResponseStatus.class);

        if (resolvable != null && an != null) {
            String message = messageSource.getMessage(resolvable, Locale.getDefault());
            return new ResponseEntity(message, an.value());
        } else {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (an != null) {
                status = an.value();
            }
            return new ResponseEntity(e.getMessage(), status);
        }
    }

    @ExceptionHandler(CmisContentAlreadyExistsException.class)
    public ResponseEntity cmisAlreayExistsHandler() {
        return new ResponseEntity<String>(HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CmisPermissionDeniedException.class)
    public ResponseEntity cmisPermissionDeniedHandler() {
        return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CmisObjectNotFoundException.class)
    public ResponseEntity cmisObjectNotFoundHandler() {
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity IOErrorHandler() {
        return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

}
