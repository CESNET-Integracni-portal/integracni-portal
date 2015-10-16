package cz.cvut.fel.integracniportal.mvc;

import cz.cvut.fel.integracniportal.exceptions.BaseException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    private static final org.apache.log4j.Logger log = Logger.getLogger(ControllerExceptionHandler.class);

    @Autowired
    protected MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public void otherException(Exception e) throws Exception {
        e.printStackTrace();
        throw e;
    }

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

    @ExceptionHandler(IOException.class)
    public ResponseEntity IOErrorHandler() {
        return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        log.warn("Returning HTTP 400 Bad Request", e);
    }

}
