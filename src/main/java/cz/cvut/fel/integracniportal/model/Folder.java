package cz.cvut.fel.integracniportal.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity for folder.
 */
@Entity
@Table(name = "resource_folder", uniqueConstraints = @UniqueConstraint(columnNames = {"parent", "name"}))
@PrimaryKeyJoinColumn(name = "folder_id", referencedColumnName = "node_id")
public class Folder extends AbstractNode {

    @OneToMany(mappedBy = "parent")
    private List<AbstractNode> subnodes;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "online")
    private boolean online = true;


    public List<Folder> getFolders() {
        List<Folder> folders = new ArrayList<Folder>();
        for (AbstractNode node : this.subnodes) {
            node.getFolderNode(folders);
        }
        return folders;
    }

    public List<FileMetadata> getFiles() {
        List<FileMetadata> files = new ArrayList<FileMetadata>();
        for (AbstractNode node : this.subnodes) {
            node.getFileNode(files);
        }
        return files;
    }

    //TODO: setters for files and folder. Is it necessary

    public List<AbstractNode> getSubnodes() {
        return subnodes;
    }

    public void setSubnodes(List<AbstractNode> subnodes) {
        this.subnodes = subnodes;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public void getFileNode(List<FileMetadata> context) {
        //Empty
    }

    @Override
    public void getFolderNode(List<Folder> context) {
        context.add(this);
    }
}
