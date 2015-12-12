package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.*;

@Entity
@DiscriminatorValue("FILE")
public class FileMetadata extends Node {

    @Column(name = "mimetype", nullable = true)
    private String mimetype;

    @Column(name = "filesize", nullable = true)
    private Long filesize;

    @Column(name = "archive_on", nullable = true)
    private Date archiveOn;

    @Column(name = "delete_on", nullable = true)
    private Date deleteOn;

    @Column(name = "online", nullable = true)
    private boolean online;

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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}
