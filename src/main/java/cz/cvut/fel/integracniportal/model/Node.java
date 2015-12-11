package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance
@DiscriminatorColumn(name = "node_type")
@Table(name = "resource_node", uniqueConstraints = @UniqueConstraint(columnNames = {"parent", "name"}))
public class Node extends AbstractEntity<Long> {

    @Id
    @Column(name = "node_id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long nodeId;

    @Column(name = "space")
    private String space;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent", referencedColumnName = "node_id")
    private Folder parent;

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
    @JoinTable(name = "resource_node_label", joinColumns = {@JoinColumn(name = "node_id")},
            inverseJoinColumns = {@JoinColumn(name = "label_id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"label_id", "node_id"})})
    private List<Label> labels;

    @ManyToOne
    @JoinColumn(name = "acl_parent_id")
    private Node aclParent;

    @OneToMany(mappedBy = "targetFolder")
    private List<AccessControlEntry> acEntries;

    @Override
    public Long getId() {
        return nodeId;
    }

    @Override
    public void setId(Long id) {
        this.nodeId = id;
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

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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

    public Node getAclParent() {
        return aclParent;
    }

    public void setAclParent(Node aclParent) {
        this.aclParent = aclParent;
    }

    public List<AccessControlEntry> getAcEntries() {
        return acEntries;
    }

    public void setAcEntries(List<AccessControlEntry> acEntries) {
        this.acEntries = acEntries;
    }
}
