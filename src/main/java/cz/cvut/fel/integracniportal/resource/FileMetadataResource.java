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

    private String uuid;

    private String filename;

    private String mimetype;

    private Long filesize;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZZZZZ")
    private Date createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZZZZZ")
    private Date changedOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZZZZZ")
    private Date archiveOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZZZZZ")
    private Date deleteOn;

    private FileState state;

    public FileMetadataResource() {}

    public FileMetadataResource(FileMetadata fileMetadata, CesnetFileMetadata cesnetFileMetadata) {
        uuid = fileMetadata.getUuid();
        filename = fileMetadata.getFilename();
        mimetype = fileMetadata.getMimetype();
        filesize = cesnetFileMetadata.getFilesize();
        createdOn = fileMetadata.getCreatedOn();
        changedOn = fileMetadata.getChangedOn();
        archiveOn = fileMetadata.getArchiveOn();
        deleteOn = fileMetadata.getDeleteOn();
        state = cesnetFileMetadata.getState();
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

    public Date getArchiveOn() {
        return archiveOn;
    }

    public void setArchiveOn(Date archiveOn) {
        this.archiveOn = archiveOn;
    }

    public Date getDeleteOn() {
        return deleteOn;
    }

    public void setDeleteOn(Date deleteOn) {
        this.deleteOn = deleteOn;
    }

    public FileState getState() {
        return state;
    }

    public void setState(FileState state) {
        this.state = state;
    }
}
