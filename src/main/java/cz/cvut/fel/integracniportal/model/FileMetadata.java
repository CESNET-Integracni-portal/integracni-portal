package cz.cvut.fel.integracniportal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Entity for file metadata.
 */
@Entity
@Table(name = "resource_file", uniqueConstraints = @UniqueConstraint(columnNames = {"parent", "name"}))
@PrimaryKeyJoinColumn(name = "file_metadata_id", referencedColumnName = "node_id")
public class FileMetadata extends AbstractNode {

    @Column(name = "mimetype", nullable = false)
    private String mimetype;

    @Column(name = "filesize", nullable = false)
    private Long filesize;

    @Column(name = "archive_on", nullable = true)
    private Date archiveOn;

    @Column(name = "delete_on", nullable = true)
    private Date deleteOn;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "online")
    private boolean online;

    public String getFilename() {
        return this.getName();
    }

    public void setFilename(String name) {
        this.setName(name);
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

    @Override
    public void getFileNode(List<FileMetadata> context) {
        context.add(this);
    }

    @Override
    public void getFolderNode(List<Folder> context) {
        //Empty
    }
}
