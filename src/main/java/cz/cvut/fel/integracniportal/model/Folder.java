package cz.cvut.fel.integracniportal.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue("FOLDER")
public class Folder extends Node {

    @OneToMany(mappedBy = "parent")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.DELETE})
    private List<Node> folders;

    @OneToMany(mappedBy = "parent")
    private List<Node> files;

    @Column(name = "online")
    private boolean online = true;

    public Folder() {
        this.folders = new ArrayList<Node>();
        this.files = new ArrayList<Node>();
    }

    public List<Folder> getFolders() {
        List<Folder> folders = new ArrayList<Folder>();

        for (Node node : this.folders) {
            if (node instanceof Folder) {
                folders.add((Folder) node);
            }
        }

        return folders;
    }

    public List<FileMetadata> getFiles() {
        List<FileMetadata> files = new ArrayList<FileMetadata>();

        for (Node node : this.files) {
            if (node instanceof FileMetadata) {
                files.add((FileMetadata) node);
            }
        }

        return files;
    }

    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public void setOnline(boolean online) {
        this.online = online;
    }
}
