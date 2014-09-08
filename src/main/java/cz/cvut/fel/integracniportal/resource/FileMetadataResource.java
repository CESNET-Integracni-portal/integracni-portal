package cz.cvut.fel.integracniportal.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata;
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
    private Date created;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZZZZZ")
    private Date archiveOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZZZZZ")
    private Date deleteOn;

    private String state;

    public FileMetadataResource() {}

    public FileMetadataResource(FileMetadata fileMetadata, CesnetFileMetadata cesnetFileMetadata) {
        uuid = fileMetadata.getUuid();
        filename = fileMetadata.getFilename();
        mimetype = fileMetadata.getMimetype();
        filesize = cesnetFileMetadata.getFilesize();
        created = cesnetFileMetadata.getCreated();
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
