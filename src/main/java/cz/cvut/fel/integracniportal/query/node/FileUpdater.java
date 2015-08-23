package cz.cvut.fel.integracniportal.query.node;

import cz.cvut.fel.integracniportal.dao.FileMetadataDao;
import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.domain.node.events.FileCreatedEvent;
import cz.cvut.fel.integracniportal.domain.node.events.FileMovedEvent;
import cz.cvut.fel.integracniportal.domain.node.events.FileRenamedEvent;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Radek Jezdik
 */
@Component
public class FileUpdater {

    @Autowired
    private FileMetadataDao fileDao;

    @Autowired
    private UserDetailsDao userDao;

    @Autowired
    private FolderDao folderDao;

    @EventHandler
    public void createFile(FileCreatedEvent event) {
        UserDetails owner = userDao.getReference(event.getOwner().getId());

        Folder parentFolder = event.getParentFolder() != null
                ? folderDao.load(event.getParentFolder().getId())
                : null;

        FileMetadata file = new FileMetadata();
        file.setId(event.getId().getId());
        file.setOwner(owner);
        file.setFilename(event.getName());
        file.setSpace(event.getSpace());
        file.setMimetype(event.getMimetype());
        file.setFilesize(event.getSize());
        file.setParent(parentFolder);

        file.setOnline(true);
        //TODO file.setOnline(event.getFileState());

        fileDao.createFileMetadata(file);
    }

    @EventHandler
    public void renameFile(FileRenamedEvent event) {
        FileMetadata file = fileDao.load(event.getId().getId());

        file.setFilename(event.getNewName());

        fileDao.update(file);
    }

    @EventHandler
    public void moveFile(FileMovedEvent event) {
        FileMetadata file = fileDao.load(event.getId().getId());
        Folder folder = folderDao.load(event.getNewParent().getId());

        file.setParent(folder);

        fileDao.update(file);
    }

}
