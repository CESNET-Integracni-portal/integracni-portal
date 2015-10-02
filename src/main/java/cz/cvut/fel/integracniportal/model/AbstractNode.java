package cz.cvut.fel.integracniportal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * FileMetadata and Folder superclass
 *
 * @author Eldar Iosip
 */
@Entity
@Table(name = "node")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractNode extends AbstractEntity<String> {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "node_id", unique = true)

    private String nodeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "space")
    private String space;

    @ManyToOne
    @JoinColumn(name = "parent", referencedColumnName = "folder_id")
    private Folder parent;

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "user_id")
    private UserDetails owner;

    @Column(name = "created_on", nullable = false)
    private Date createdOn;

    @Column(name = "changed_on", nullable = false)
    private Date changedOn;

    @ManyToMany
    @JoinTable(name = "resource_node_label", joinColumns = {@JoinColumn(name = "node_id")},
            inverseJoinColumns = {@JoinColumn(name = "label_id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"label_id", "node_id"})})
    private List<Label> labels;

    @OneToMany(mappedBy = "owner")
    private List<AclPermission> acl;

    @ManyToOne
    @JoinColumn(name = "acl_parent_id")
    private AbstractNode aclParent;

    public String getId() {
        return nodeId;
    }

    public void setId(String id) {
        this.nodeId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
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

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<AclPermission> getAcl() {
        return acl;
    }

    public void setAcl(List<AclPermission> acl) {
        this.acl = acl;
    }

    public AbstractNode getAclParent() {
        return aclParent;
    }

    public void setAclParent(AbstractNode aclParent) {
        this.aclParent = aclParent;
    }

    public abstract void getFileNode(List<FileMetadata> context);

    public abstract void getFolderNode(List<Folder> context);
}
