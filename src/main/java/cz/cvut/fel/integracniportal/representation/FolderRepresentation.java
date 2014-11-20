package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;

import java.util.*;

/**
 * Resource class for folder.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FolderRepresentation {

    private String id;

    private String name;

    private List<Map<String, String>> breadcrumbs;

    private List<FolderRepresentation> folders;

    private List<FileMetadataRepresentation> files;

    private UserDetailsRepresentation owner;

    private Date createdOn;

    private Date changedOn;

    public FolderRepresentation() {}

    public FolderRepresentation(org.apache.chemistry.opencmis.client.api.Folder folder) {
        id = folder.getId();
        name = folder.getName();
        breadcrumbs = new ArrayList<Map<String, String>>();
        org.apache.chemistry.opencmis.client.api.Folder currentParent = folder.getFolderParent();
        while (currentParent != null) {
            Map<String, String> breadcrumbEntry = new HashMap<String, String>();
            breadcrumbEntry.put("id", currentParent.getId());
            breadcrumbEntry.put("name", currentParent.getName());
            breadcrumbs.add(breadcrumbEntry);
            currentParent = currentParent.getFolderParent();
        }
    }

    public FolderRepresentation(Folder folder) {
        this(folder, true);
    }

    public FolderRepresentation(Folder folder, boolean deepCopy) {
        id = folder.getFolderId().toString();
        name = folder.getName();
        if (folder.getOwner() != null) {
            owner = new UserDetailsRepresentation();
            owner.setId(folder.getOwner().getUserId());
            owner.setUsername(folder.getOwner().getUsername());
        }
        createdOn = folder.getCreatedOn();
        changedOn = folder.getChangedOn();
        if (deepCopy) {
            breadcrumbs = new ArrayList<Map<String, String>>();
            Folder currentParent = folder.getParent();
            while (currentParent != null) {
                Map<String, String> breadcrumbEntry = new HashMap<String, String>();
                breadcrumbEntry.put("id", currentParent.getFolderId().toString());
                breadcrumbEntry.put("name", currentParent.getName());
                breadcrumbs.add(breadcrumbEntry);
                currentParent = currentParent.getParent();
            }
            folders = new ArrayList<FolderRepresentation>(folder.getFolders().size());
            for (Folder subFolder : folder.getFolders()) {
                FolderRepresentation folderResource = new FolderRepresentation(subFolder, false);
                folders.add(folderResource);
            }
            files = new ArrayList<FileMetadataRepresentation>(folder.getFiles().size());
            for (FileMetadata fileMetadata : folder.getFiles()) {
                FileMetadataRepresentation fileMetadataResource = new FileMetadataRepresentation(fileMetadata);
                files.add(fileMetadataResource);
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, String>> getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(List<Map<String, String>> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public List<FolderRepresentation> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderRepresentation> folders) {
        this.folders = folders;
    }

    public List<FileMetadataRepresentation> getFiles() {
        return files;
    }

    public void setFiles(List<FileMetadataRepresentation> files) {
        this.files = files;
    }

    public UserDetailsRepresentation getOwner() {
        return owner;
    }

    public void setOwner(UserDetailsRepresentation owner) {
        this.owner = owner;
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
