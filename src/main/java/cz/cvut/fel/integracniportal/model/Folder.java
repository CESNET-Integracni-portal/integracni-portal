package cz.cvut.fel.integracniportal.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity for folder.
 */
@Entity
public class Folder extends Node {

    @OneToMany(mappedBy = "parent")
    private List<Node> childNodes;

    public List<Node> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<Node> childNodes) {
        this.childNodes = childNodes;
    }

    @Transient
    public List<Folder> getFolders() {
        List<Folder> folders = new ArrayList<Folder>();

        for (Node node : getChildNodes()) {
            if (node instanceof Folder) {
                folders.add((Folder) node);
            }
        }

        return folders;
    }

    @Transient
    public List<FileMetadata> getFiles() {
        List<FileMetadata> folders = new ArrayList<FileMetadata>();

        for (Node node : getChildNodes()) {
            if (node instanceof FileMetadata) {
                folders.add((FileMetadata) node);
            }
        }

        return folders;
    }

}