package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.cmis.AlfrescoUtils;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

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

    public FolderRepresentation() {
    }

    public FolderRepresentation(org.apache.chemistry.opencmis.client.api.Folder folder, org.apache.chemistry.opencmis.client.api.Folder topFolder) {
        this(folder, topFolder, true);
    }

    public FolderRepresentation(org.apache.chemistry.opencmis.client.api.Folder folder, org.apache.chemistry.opencmis.client.api.Folder topFolder, boolean deepCopy) {
        id = AlfrescoUtils.parseId(folder);
        name = folder.getName();
        createdOn = folder.getCreationDate().getTime();
        changedOn = folder.getLastModificationDate().getTime();

        if (deepCopy) {
            // Generate breadcrumbs
            breadcrumbs = new ArrayList<Map<String, String>>();
            if (!folder.equals(topFolder)) {
                generateBreadcrumbs(breadcrumbs, folder.getFolderParent(), topFolder);
                Collections.reverse(breadcrumbs);
            }

            folders = new ArrayList<FolderRepresentation>();
            files = new ArrayList<FileMetadataRepresentation>();
            for (CmisObject child : folder.getChildren()) {
                if (child.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
                    folders.add(new FolderRepresentation((org.apache.chemistry.opencmis.client.api.Folder) child, topFolder, false));
                } else if (child.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
                    files.add(new FileMetadataRepresentation((Document) child));
                }
            }
        }
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

        if (deepCopy) {
            // Generate breadcrumbs
            breadcrumbs = new ArrayList<Map<String, String>>();
            generateBreadcrumbs(breadcrumbs, folder.getParent());
            Collections.reverse(breadcrumbs);

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

    public static void generateBreadcrumbs(List<Map<String, String>> breadcrumbs, org.apache.chemistry.opencmis.client.api.Folder folder, org.apache.chemistry.opencmis.client.api.Folder topFolder) {
        org.apache.chemistry.opencmis.client.api.Folder current = folder;
        while (current != null && !current.getId().equals(topFolder.getId())) {
            Map<String, String> breadcrumbEntry = new HashMap<String, String>();
            breadcrumbEntry.put("id", AlfrescoUtils.parseId(current));
            breadcrumbEntry.put("name", current.getName());
            breadcrumbs.add(breadcrumbEntry);
            current = current.getFolderParent();
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

}
