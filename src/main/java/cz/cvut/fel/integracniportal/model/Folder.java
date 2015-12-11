package cz.cvut.fel.integracniportal.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "resource_folder")
@DiscriminatorValue("FOLDER")
public class Folder extends Node {

    @OneToMany(mappedBy = "parent")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.DELETE})
    private List<Node> folders;

    @OneToMany(mappedBy = "parent")
    private List<FileMetadata> files;

    public Folder() {
        this.folders = new ArrayList<Node>();
        this.files = new ArrayList<FileMetadata>();
    }

    public List<Folder> getFolders() {
        List<Folder> folders = new ArrayList<Folder>();

        for (Node node : this.folders) {
            folders.add((Folder) node);
        }

        return folders;
    }

    public List<FileMetadata> getFiles() {
        return files;
    }

    public void setFiles(List<FileMetadata> files) {
        this.files = files;
    }
}
