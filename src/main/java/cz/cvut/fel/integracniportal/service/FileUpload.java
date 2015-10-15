package cz.cvut.fel.integracniportal.service;

import org.apache.commons.io.input.CountingInputStream;

import java.io.InputStream;

/**
 * @author Radek Jezdik
 */
public class FileUpload {

    private String fileName;

    private String contentType;

    private CountingInputStream inputStream;

    public FileUpload(String fileName, String contentType, InputStream inputStream) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.inputStream = new CountingInputStream(inputStream);
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public long getByteReadCount() {
        return inputStream.getByteCount();
    }
}
