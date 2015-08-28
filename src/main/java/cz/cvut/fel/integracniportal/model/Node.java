package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Abstract super class representing a node in our file system.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"parent", "name"}))
public class Node extends AbstractEntity<String> {

    @Id
    @Column(name = "node_id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent", referencedColumnName = "node_id")
    private Folder parent;

    @Column(name = "space")
    private String space;

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "user_id")
    private UserDetails owner;

    @Column(name = "created_on", nullable = false)
    private Date createdOn;

    @Column(name = "changed_on", nullable = false)
    private Date changedOn;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "deleted_on")
    private Date deletedOn;

    @Column(name = "online")
    private boolean online = true;

    @ManyToMany
    @JoinTable(name = "resource_folder_label", joinColumns = {@JoinColumn(name = "folder_id")},
            inverseJoinColumns = {@JoinColumn(name = "label_id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"label_id", "folder_id"})})
    private List<Label> labels;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String folderId) {
        this.id = folderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public UserDetails getOwner() {
        return owner;
    }

    public void setOwner(UserDetails owner) {
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Date deletedOn) {
        this.deletedOn = deletedOn;
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
