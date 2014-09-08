package cz.cvut.fel.integracniportal.cesnet;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Class for file metadata received from Cesnet.
 */
public class CesnetFileMetadata {
    private String filename;
    private Long filesize;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZZZZZ")
    private Date created;
    private String state;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
