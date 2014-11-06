package cz.cvut.fel.integracniportal.exceptions;

import org.springframework.context.MessageSourceResolvable;

public class FileAccessException extends AbstractCustomException {

    public FileAccessException(MessageSourceResolvable e) {
        super(e);
    }

    public FileAccessException(String code) {
        super(code);
    }

    public FileAccessException(String code, Object... args) {
        super(code, args);
    }
}
