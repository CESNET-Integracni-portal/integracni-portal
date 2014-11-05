package cz.cvut.fel.integracniportal.representation;

import cz.cvut.fel.integracniportal.model.FileMetadata;

import java.util.Date;

/**
 * Resource class for file metadata.
 */
public class FileMetadataRepresentation {

    private String uuid;

    private String filename;

    private String mimetype;

    private Long filesize;

    private Date createdOn;

    private Date changedOn;

    public FileMetadataRepresentation() {}

    public FileMetadataRepresentation(FileMetadata fileMetadata) {
        uuid = fileMetadata.getUuid();
        filename = fileMetadata.getFilename();
        mimetype = fileMetadata.getMimetype();
        filesize = fileMetadata.getFilesize();
        createdOn = fileMetadata.getCreatedOn();
        changedOn = fileMetadata.getChangedOn();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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
