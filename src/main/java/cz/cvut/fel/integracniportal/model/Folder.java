package cz.cvut.fel.integracniportal.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue("FOLDER")
public class Folder extends Node {

    @Column(name = "online")
    private boolean online = true;

    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public void getFileMetadataNode(List<FileMetadata> context) {
        //nothing
    }

    @Override
    public void getFolderNode(List<Folder> context) {
        context.add(this);
    }

    @Override
    public boolean isFolder() {
        return true;
    }
}
