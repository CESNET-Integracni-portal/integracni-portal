package cz.cvut.fel.integracniportal.api;

import java.util.Date;

/**
 * @author Radek Jezdik
 */
public class FileDefinition {

    private Long id;

    private String name;

    private User owner;

    private Long size;

    private String mimeType;

    private Date dateCreated;

    private FolderDefinition folder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public FolderDefinition getFolder() {
        return folder;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setFolder(FolderDefinition folder) {
        this.folder = folder;
    }
}
