package cz.cvut.fel.integracniportal.exceptions;


public class ZipStreamException extends BaseException {

    public ZipStreamException(String code){
        super(code);
    }

    public ZipStreamException(String code, Throwable e){
        super(code,e);
    }
}
