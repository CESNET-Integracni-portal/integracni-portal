package cz.cvut.fel.integracniportal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity for file metadata.
 */
@Entity
@Table(name = "resource_file", uniqueConstraints = @UniqueConstraint(columnNames = {"parent", "filename"}))
public class FileMetadata extends AbstractEntity<String> {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "mimetype", nullable = false)
    private String mimetype;

    @Column(name = "filesize", nullable = false)
    private Long filesize;

    @ManyToOne
    @JoinColumn(name = "parent", referencedColumnName = "folder_id")
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
}
