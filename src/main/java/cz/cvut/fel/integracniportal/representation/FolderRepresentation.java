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

    private long folderId;

    private String name;

    private List<Map<String, String>> breadcrumbs;

    private List<FolderRepresentation> folders;

    private List<FileMetadataRepresentation> files;

    private Date createdOn;

    private Date changedOn;

    public FolderRepresentation() {}

    public FolderRepresentation(Folder folder) {
        this(folder, true);
    }

    public FolderRepresentation(Folder folder, boolean deepCopy) {
        folderId = folder.getFolderId();
        name = folder.getName();
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

    private void createFromFolder(Folder folder, boolean deepCopy) {
        folderId = folder.getFolderId();
        name = folder.getName();
        if (deepCopy) {
            breadcrumbs = new ArrayList<Map<String, String>>();
            Folder currentParent = folder.getParent();
            while (currentParent != null) {
                Map<String, String> breadcrumbEntry = new HashMap<String, String>();
                breadcrumbEntry.put("id", currentParent.getFolderId().toString());
                breadcrumbEntry.put("name", currentParent.getName());
                breadcrumbs.add(breadcrumbEntry);
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

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
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
