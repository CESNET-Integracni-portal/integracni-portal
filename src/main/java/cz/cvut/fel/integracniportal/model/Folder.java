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

        if (getChildNodes() == null) {
            return folders;
        }

        for (Node node : getChildNodes()) {
            if (node instanceof Folder) {
                folders.add((Folder) node);
            }
        }

        return folders;
    }

    @Transient
    public List<FileMetadata> getFiles() {
        List<FileMetadata> files = new ArrayList<FileMetadata>();

        if (getChildNodes() == null) {
            return files;
        }

        for (Node node : getChildNodes()) {
            if (node instanceof FileMetadata) {
                files.add((FileMetadata) node);
            }
        }

        return files;
    }

}