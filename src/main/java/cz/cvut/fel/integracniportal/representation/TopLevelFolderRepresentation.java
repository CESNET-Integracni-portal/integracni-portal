package cz.cvut.fel.integracniportal.representation;

import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Radek Jezdik
 */
public class TopLevelFolderRepresentation {

    private List<FolderRepresentation> folders;

    private List<FileMetadataRepresentation> files;

    public TopLevelFolderRepresentation(List<Folder> topLevelFolders, List<FileMetadata> topLevelFiles, UserDetails viewer) {
        folders = new ArrayList<FolderRepresentation>(topLevelFolders.size());
        for (Folder subFolder : topLevelFolders) {
            FolderRepresentation folderResource = new FolderRepresentation(subFolder, viewer, false);
            folders.add(folderResource);
        }

        files = new ArrayList<FileMetadataRepresentation>(topLevelFiles.size());
        for (FileMetadata fileMetadata : topLevelFiles) {
            FileMetadataRepresentation fileMetadataResource = new FileMetadataRepresentation(fileMetadata);
            files.add(fileMetadataResource);
        }
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
}
