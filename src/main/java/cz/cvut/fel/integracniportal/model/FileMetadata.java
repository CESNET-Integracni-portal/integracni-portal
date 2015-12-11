package cz.cvut.fel.integracniportal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

/**
 * Entity for file metadata.
 */
@Entity
@Table(name = "resource_file", uniqueConstraints = @UniqueConstraint(columnNames = {"parent", "name"}))
public class FileMetadata extends AbstractEntity<String> {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "space")
    private String space;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mimetype", nullable = false)
    private String mimetype;

    @Column(name = "filesize", nullable = false)
    private Long filesize;

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

    @Column(name = "archive_on", nullable = true)
    private Date archiveOn;

    @Column(name = "delete_on", nullable = true)
    private Date deleteOn;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "online")
    private boolean online;

    @ManyToMany
    @JoinTable(name = "resource_file_label", joinColumns = {@JoinColumn(name = "uuid")},
            inverseJoinColumns = {@JoinColumn(name = "label_id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"label_id", "uuid"})})
    private List<Label> labels;

    @ManyToOne
    @JoinColumn(name = "acl_parent_id")
    private Folder aclParent;

    @OneToMany(mappedBy = "targetFile")
    private List<AccessControlEntry> acEntries;

    public FileMetadata() {
        this.acEntries = new ArrayList<AccessControlEntry>();
    }

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
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

    public Date getArchiveOn() {
        return archiveOn;
    }

    public void setArchiveOn(Date archiveOn) {
        this.archiveOn = archiveOn;
    }

    public Date getDeleteOn() {
        return deleteOn;
    }

    public void setDeleteOn(Date deleteOn) {
        this.deleteOn = deleteOn;
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

    public Folder getAclParent() {
        return aclParent;
    }

    public void setAclParent(Folder aclParent) {
        this.aclParent = aclParent;
    }

    public List<AccessControlEntry> getAcEntries() {
        return acEntries;
    }

    public void setAcEntries(List<AccessControlEntry> acEntries) {
        this.acEntries = acEntries;
    }


    public void addAclPermission(AccessControlEntry accessControlEntry) {
        if (accessControlEntry.getTargetFile() != this) {
            accessControlEntry.setTargetFile(this);
        }
        this.acEntries.add(accessControlEntry);
    }

}
