package cz.cvut.fel.integracniportal.query.node;

import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.domain.node.events.*;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Radek Jezdik
 */
@Component
public class FolderUpdater {

    @Autowired
    private FolderDao folderDao;

    @Autowired
    private UserDetailsDao userDao;

    @EventHandler
    public void createFolder(FolderCreatedEvent event) {
        UserDetails owner = userDao.getReference(event.getOwner().getId());

        Folder parentFolder = event.getParentFolder() != null
                ? folderDao.load(event.getParentFolder().getId())
                : null;

        Folder folder = new Folder();
        folder.setId(event.getId().getId());
        folder.setOwner(owner);
        folder.setName(event.getName());
        folder.setSpace(event.getSpace());
        folder.setParent(parentFolder);

        folderDao.createFolder(folder);
    }

    @EventHandler
    public void renameFolder(FolderRenamedEvent event) {
        Folder folder = folderDao.load(event.getId().getId());

        folder.setName(event.getNewName());

        folderDao.update(folder);
    }

    @EventHandler
    public void moveFolder(FolderMovedEvent event) {
        Folder folder = folderDao.load(event.getId().getId());
        Folder newParent = folderDao.load(event.getNewParent().getId());

        folder.setParent(newParent);

        folderDao.update(folder);
    }

    @EventHandler
    public void moveFolder(FolderMovedToRootEvent event) {
        Folder folder = folderDao.load(event.getId().getId());

        folder.setParent(null);

        folderDao.update(folder);
    }

    @EventHandler
    public void deleteFolder(FolderDeletedEvent event) {
        Folder folder = folderDao.load(event.getId().getId());

        folderDao.delete(folder);
    }

}
