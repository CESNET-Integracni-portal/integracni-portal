package cz.cvut.fel.integracniportal.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata;
import cz.cvut.fel.integracniportal.cesnet.FileState;
import cz.cvut.fel.integracniportal.model.FileMetadata;

import java.util.Date;

/**
 * Resource class for file metadata.
 */
public class CesnetFileMetadataResource extends FileMetadataResource {

    private String uuid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZZZZZ")
    private Date archiveOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZZZZZ")
    private Date deleteOn;

    private FileState state;

    public CesnetFileMetadataResource() {}

    public CesnetFileMetadataResource(FileMetadata fileMetadata, CesnetFileMetadata cesnetFileMetadata) {
        uuid = fileMetadata.getUuid();
        archiveOn = fileMetadata.getArchiveOn();
        deleteOn = fileMetadata.getDeleteOn();
        state = cesnetFileMetadata.getState();
        setFilename(fileMetadata.getFilename());
        setMimetype(fileMetadata.getMimetype());
        setFilesize(cesnetFileMetadata.getFilesize());
        setCreatedOn(fileMetadata.getCreatedOn());
        setChangedOn(fileMetadata.getChangedOn());
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
