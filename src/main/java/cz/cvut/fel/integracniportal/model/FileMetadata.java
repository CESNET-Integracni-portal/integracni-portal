package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.*;

@Entity
@DiscriminatorValue("FILE")
public class FileMetadata extends Node {

    @Column(name = "mimetype")
    private String mimetype;

    @Column(name = "filesize")
    private Long filesize;

    @Column(name = "archive_on")
    private Date archiveOn;

    @Column(name = "delete_on")
    private Date deleteOn;

    @Column(name = "online")
    private boolean online;

    @Column(name = "salt")
    private String salt;

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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public void getFileMetadataNode(List<FileMetadata> context) {
        context.add(this);
    }

    @Override
    public void getFolderNode(List<Folder> context) {
        //nothing
    }

    @Override
    public boolean isFolder() {
        return false;
    }

}
