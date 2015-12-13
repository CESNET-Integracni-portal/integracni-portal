package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Inheritance
@DiscriminatorColumn(name = "node_type")
@Table(name = "resource_node", uniqueConstraints = @UniqueConstraint(columnNames = {"parent", "name"}))
public abstract class Node extends AbstractEntity<Long> {

    @Id
    @Column(name = "node_id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long nodeId;

    @Column(name = "space", nullable = false)
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

    @ManyToMany
    @JoinTable(name = "resource_node_label", joinColumns = {@JoinColumn(name = "node_id")},
            inverseJoinColumns = {@JoinColumn(name = "label_id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"label_id", "node_id"})})
    private List<Label> labels;

    @ManyToOne
    @JoinColumn(name = "ac_parent_folder_id")
    private Node acParent;

    @OneToMany(mappedBy = "acParent")
    private Set<Node> acSubparents;

    @OneToMany(mappedBy = "targetNode")
    private List<AccessControlEntry> acEntries;

    public Node() {
        this.labels = new ArrayList<Label>();
        this.acSubparents = new HashSet<Node>();
        this.acEntries = new ArrayList<AccessControlEntry>();
    }

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

    public abstract boolean isOnline();

    public abstract void setOnline(boolean online);

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public Node getAcParent() {
        return acParent;
    }

    public void setAcParent(Node acParent) {
        this.acParent = acParent;
    }

    public List<AccessControlEntry> getAcEntries() {
        return acEntries;
    }

    public void setAcEntries(List<AccessControlEntry> acEntries) {
        this.acEntries = acEntries;
    }

    public Set<Node> getAcSubparents() {
        return acSubparents;
    }

    public void setAcSubparents(Set<Node> acSubparents) {
        this.acSubparents = acSubparents;
    }

    public abstract void getFileMetadataNode(List<FileMetadata> context);

    public abstract void getFolderNode(List<Folder> context);

    /*
    public void addAccessControlEntry(AccessControlEntry accessControlEntry) {
        if (accessControlEntry.getTargetFile() != this) {
            accessControlEntry.setTargetFile(this);
        }
        this.acEntries.add(accessControlEntry);
    }
    */
}
