package cz.cvut.fel.integracniportal.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata;
import cz.cvut.fel.integracniportal.cesnet.FileState;
import cz.cvut.fel.integracniportal.model.FileMetadata;

import java.util.Date;

/**
 * Resource class for file metadata.
 */
public class FileMetadataResource {

    private String filename;

    private String mimetype;

    private Long filesize;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZZZZZ")
    private Date createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZZZZZ")
    private Date changedOn;

    public FileMetadataResource() {}

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getChangedOn() {
        return changedOn;
    }

    public void setChangedOn(Date changedOn) {
        this.changedOn = changedOn;
    }

}
