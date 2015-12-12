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
    private List<Node> subnodes;

    @Column(name = "online")
    private boolean online = true;

    public Folder() {
        this.subnodes = new ArrayList<Node>();
    }

    public List<Folder> getFolders() {
        List<Folder> folders = new ArrayList<Folder>();

        for (Node node : this.subnodes) {
            node.getFolderNode(folders);
        }

        return folders;
    }

    public List<FileMetadata> getFiles() {
        List<FileMetadata> files = new ArrayList<FileMetadata>();

        for (Node node : this.subnodes) {
            node.getFileMetadataNode(files);
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

    @Override
    public void getFileMetadataNode(List<FileMetadata> context) {
        //nothing
    }

    @Override
    public void getFolderNode(List<Folder> context) {
        context.add(this);
    }
}
