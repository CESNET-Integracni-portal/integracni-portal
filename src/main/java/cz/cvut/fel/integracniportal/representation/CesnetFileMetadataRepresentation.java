package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata;
import cz.cvut.fel.integracniportal.cesnet.FileState;
import cz.cvut.fel.integracniportal.model.FileMetadata;

import java.util.Date;

/**
 * Representation class for file metadata.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CesnetFileMetadataRepresentation extends FileMetadataRepresentation {

    private Date archiveOn;

    private Date deleteOn;

    private FileState state;

    public CesnetFileMetadataRepresentation() {}

    public CesnetFileMetadataRepresentation(FileMetadata fileMetadata, CesnetFileMetadata cesnetFileMetadata) {
        super(fileMetadata);
        archiveOn = fileMetadata.getArchiveOn();
        deleteOn = fileMetadata.getDeleteOn();
        state = cesnetFileMetadata.getState();
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
