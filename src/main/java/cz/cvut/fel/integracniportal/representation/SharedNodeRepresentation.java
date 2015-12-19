package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

/**
 * Holds a set of nodes, shared with current logged user.
 *
 * @author Eldar Iosip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedNodeRepresentation {

    private Set<FolderRepresentation> folders;

    private Set<FileMetadataRepresentation> files;

    public SharedNodeRepresentation() {
    }

    public SharedNodeRepresentation(Set<FolderRepresentation> folders, Set<FileMetadataRepresentation> files) {
        this.folders = folders;
        this.files = files;
    }

    public Set<FolderRepresentation> getFolders() {
        return folders;
    }

    public Set<FileMetadataRepresentation> getFiles() {
        return files;
    }
}
