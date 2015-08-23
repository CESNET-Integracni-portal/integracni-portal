package cz.cvut.fel.integracniportal.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Entity for folder.
 */
@Entity
@Table(name = "resource_folder", uniqueConstraints = @UniqueConstraint(columnNames = {"parent", "name"}))
public class Folder extends AbstractEntity<String> {

    @Id
    @Column(name = "folder_id")
    private String folderId;

    @Column(name = "space")
    private String space;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent", referencedColumnName = "folder_id")
    private Folder parent;

    @OneToMany(mappedBy = "parent")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.DELETE})
    private List<Folder> folders;

    @OneToMany(mappedBy = "parent")
    private List<FileMetadata> files;

    @Column(name = "created_on", nullable = false)
    private Date createdOn;

    @Column(name = "changed_on", nullable = false)
    private Date changedOn;

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "user_id")
    private UserDetails owner;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "online")
    private boolean online = true;

    @ManyToMany
    @JoinTable(name = "resource_folder_label", joinColumns = {@JoinColumn(name = "folder_id")},
            inverseJoinColumns = {@JoinColumn(name = "label_id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"label_id", "folder_id"})})
    private List<Label> labels;

    @Override
    public String getId() {
        return folderId;
    }

    @Override
    public void setId(String folderId) {
        this.folderId = folderId;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public List<FileMetadata> getFiles() {
        return files;
    }

    public void setFiles(List<FileMetadata> files) {
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

    public UserDetails getOwner() {
        return owner;
    }

    public void setOwner(UserDetails owner) {
        this.owner = owner;
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

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }
}
