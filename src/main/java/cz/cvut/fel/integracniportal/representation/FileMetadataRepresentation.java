package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Label;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Representation class for file metadata.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileMetadataRepresentation {

    private String type = "file";

    private String uuid;

    private String filename;

    private String mimetype;

    private Long filesize;

    private UserDetailsRepresentation owner;

    private List<String> sharedWith;

    private Date createdOn;

    private Date changedOn;

    private List<LabelRepresentation> labels;

    private boolean online;


    public FileMetadataRepresentation() {
    }

    public FileMetadataRepresentation(FileMetadata fileMetadata) {
        uuid = fileMetadata.getUuid();
        filename = fileMetadata.getFilename();
        mimetype = fileMetadata.getMimetype();
        filesize = fileMetadata.getFilesize();
        if (fileMetadata.getOwner() != null) {
            owner = new UserDetailsRepresentation();
            owner.setId(fileMetadata.getOwner().getId());
            owner.setUsername(fileMetadata.getOwner().getUsername());
        }
        if (fileMetadata.getLabels() != null) {
            labels = new ArrayList<LabelRepresentation>(fileMetadata.getLabels().size());
            for (Label label: fileMetadata.getLabels()){
                LabelRepresentation labelResource = new LabelRepresentation(label);
                labels.add(labelResource);
            }
        }
        createdOn = fileMetadata.getCreatedOn();
        changedOn = fileMetadata.getChangedOn();
        online = fileMetadata.isOnline();
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

    public UserDetailsRepresentation getOwner() {
        return owner;
    }

    public void setOwner(UserDetailsRepresentation owner) {
        this.owner = owner;
    }

    public List<String> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(List<String> sharedWith) {
        this.sharedWith = sharedWith;
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

    public List<LabelRepresentation> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelRepresentation> labels) {
        this.labels = labels;
    }

    public String getType() {
        return type;
    }

    public void setOnline(boolean online){
        this.online=online;
    }

    public boolean getOnline(){
        return(online);
    }
}
