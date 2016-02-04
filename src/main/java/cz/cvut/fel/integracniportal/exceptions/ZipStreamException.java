package cz.cvut.fel.integracniportal.exceptions;

/**
 * Created by mata on 14.1.16.
 */
public class ZipStreamException extends BaseException {

    public ZipStreamException(String code){
        super(code);
    }

    public ZipStreamException(String code, Throwable e){
        super(code,e);
    }
}
