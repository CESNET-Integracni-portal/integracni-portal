package cz.cvut.fel.integracniportal.query.node;

import cz.cvut.fel.integracniportal.dao.FileMetadataDao;
import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.domain.node.events.*;
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
        file.setName(event.getName());
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

        file.setName(event.getNewName());

        fileDao.update(file);
    }

    @EventHandler
    public void moveFile(FileMovedEvent event) {
        FileMetadata file = fileDao.load(event.getId().getId());
        Folder folder = folderDao.load(event.getNewParent().getId());

        file.setParent(folder);

        fileDao.update(file);
    }

    @EventHandler
    public void moveFileToRoot(FileMovedToRootEvent event) {
        FileMetadata file = fileDao.load(event.getId().getId());
        file.setParent(null);
        fileDao.update(file);
    }

    @EventHandler
    public void deleteFile(FileDeletedEvent event) {
        FileMetadata file = fileDao.load(event.getId().getId());
        fileDao.delete(file);
    }

    @EventHandler
    public void changeSize(FileSizeChangedEvent event) {
        FileMetadata file = fileDao.load(event.getId().getId());

        file.setFilesize(event.getNewSize());

        fileDao.update(file);
    }

}
