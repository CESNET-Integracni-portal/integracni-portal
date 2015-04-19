package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.Label;

import java.util.*;

/**
 * Representation class for folder.
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

    private List<LabelRepresentation> labels;

    public FolderRepresentation() {
    }

    public FolderRepresentation(Folder folder) {
        this(folder, true);
    }

    public FolderRepresentation(Folder folder, boolean deepCopy) {
        id = folder.getId().toString();
        name = folder.getName();
        if (folder.getOwner() != null) {
            owner = new UserDetailsRepresentation();
            owner.setId(folder.getOwner().getId());
            owner.setUsername(folder.getOwner().getUsername());
        }
        createdOn = folder.getCreatedOn();
        changedOn = folder.getChangedOn();
        if (folder.getLabels() != null) {
            labels = new ArrayList<LabelRepresentation>();
            for (Label label : folder.getLabels()) {
                LabelRepresentation labelResource = new LabelRepresentation(label);
                labels.add(labelResource);
            }
        }
        if (deepCopy) {
            // Generate breadcrumbs
            breadcrumbs = new ArrayList<Map<String, String>>();
            generateBreadcrumbs(breadcrumbs, folder.getParent());
            Collections.reverse(breadcrumbs);

            if (folder.getFolders() != null) {
                folders = new ArrayList<FolderRepresentation>(folder.getFolders().size());
                for (Folder subFolder : folder.getFolders()) {
                    FolderRepresentation folderResource = new FolderRepresentation(subFolder, false);
                    folders.add(folderResource);
                }
            }

            if (folder.getFiles() != null) {
                files = new ArrayList<FileMetadataRepresentation>(folder.getFiles().size());
                for (FileMetadata fileMetadata : folder.getFiles()) {
                    FileMetadataRepresentation fileMetadataResource = new FileMetadataRepresentation(fileMetadata);
                    files.add(fileMetadataResource);
                }
            }
        }
    }

    public static void generateBreadcrumbs(List<Map<String, String>> breadcrumbs, Folder folder) {
        Folder current = folder;
        while (current != null) {
            Map<String, String> breadcrumbEntry = new HashMap<String, String>();
            breadcrumbEntry.put("id", current.getId().toString());
            breadcrumbEntry.put("name", current.getName());
            breadcrumbs.add(breadcrumbEntry);
            current = current.getParent();
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

    public List<LabelRepresentation> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelRepresentation> labels) {
        this.labels = labels;
    }
}
