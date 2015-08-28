package cz.cvut.fel.integracniportal.model;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Entity for file metadata.
 */
@Entity
public class FileMetadata extends Node {

    @Column(name = "mimetype", nullable = false)
    private String mimetype;

    @Column(name = "filesize", nullable = false)
    private Long filesize;

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

}
