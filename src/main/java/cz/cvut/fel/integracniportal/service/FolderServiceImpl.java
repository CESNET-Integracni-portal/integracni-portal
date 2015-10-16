package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.command.node.CreateFolderCommand;
import cz.cvut.fel.integracniportal.command.node.DeleteFolderCommand;
import cz.cvut.fel.integracniportal.command.node.MoveFolderCommand;
import cz.cvut.fel.integracniportal.command.node.RenameFolderCommand;
import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.FolderRepresentation;
import cz.cvut.fel.integracniportal.representation.TopLevelFolderRepresentation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author Radek Jezdik
 */
@Service
@Transactional
public class FolderServiceImpl implements FolderService {

    @Autowired
    private FolderDao folderDao;

    @Autowired
    private SpaceServiceImpl fileRepositoryService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private CommandGateway commandGateway;

    @Override
    @Transactional(readOnly = true)
    public Folder getFolderById(String id) {
        Folder folder = folderDao.get(id);
        if (folder == null) {
            throw new NotFoundException("cesnet.folder.notFound", id);
        }
        return folder;
    }

    @Override
    @Transactional(readOnly = true)
    public FolderRepresentation getFolderRepresentationById(String id, UserDetails currentUser) {
        Folder folder = folderDao.getForUser(id, currentUser);
        if (folder == null) {
            throw new NotFoundException("cesnet.folder.notFound", id);
        }
        return new FolderRepresentation(folder, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> getTopLevelFolders(String spaceId, UserDetails owner) {
        return folderDao.getSpaceTopLevelFolders(spaceId, owner);
    }

    @Override
    public TopLevelFolderRepresentation getTopLevelFolder(String spaceId, UserDetails owner) {
        List<Folder> topLevelFolders = getTopLevelFolders(spaceId, owner);
        List<FileMetadata> topLevelFiles = fileMetadataService.getTopLevelFiles(spaceId, owner);

        return new TopLevelFolderRepresentation(topLevelFolders, topLevelFiles, owner);
    }

    @Override
    public TopLevelFolderRepresentation getTopLevelFolderByLabels(String spaceId, List<String> labelIds, UserDetails owner) {
        List<Folder> folders = folderDao.getFoldersByLabels(spaceId, labelIds, owner);
        List<FileMetadata> files = fileMetadataService.getFilesByLabels(spaceId, labelIds, owner);

        return new TopLevelFolderRepresentation(folders, files, owner);
    }

    @Override
    public Folder createTopLevelFolder(String folderName, String spaceId, UserDetails owner) {
        return createFolder(folderName, spaceId, null, owner);
    }

    @Override
    public Folder createSubFolder(String folderName, String parentId, UserDetails owner) {
        Folder parent = getFolderById(parentId);

        return createFolder(folderName, parent.getSpace(), FolderId.of(parentId), owner);
    }

    private Folder createFolder(String folderName, String space, FolderId parentId, UserDetails owner) {
        String id = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new CreateFolderCommand(
                new FolderId(id),
                folderName,
                parentId,
                UserId.of(owner.getId()),
                space
        ));

        return getFolderById(id);
    }

    @Override
    public Folder renameFolder(String folderId, String newName) {
        commandGateway.sendAndWait(new RenameFolderCommand(
                FolderId.of(folderId),
                newName
        ));

        return folderDao.get(folderId);
    }

    @Override
    public void deleteFolder(String folderId) {
        commandGateway.sendAndWait(new DeleteFolderCommand(
                FolderId.of(folderId)
        ));
    }

    @Override
    public void moveFolder(String folderId, String parentId) {
        commandGateway.sendAndWait(new MoveFolderCommand(
                FolderId.of(folderId),
                FolderId.of(parentId)
        ));
    }

    @Override
    public void moveFolderOnline(String folderId) {
        Folder folder = getFolderById(folderId);
        if (folder.isOnline()) {
            return;
        }
        folder.setOnline(true);
        getFileApi(folder.getSpace()).moveFolderOnline(folder);

    }

    @Override
    public void moveFolderOffline(String folderId) {
        Folder folder = getFolderById(folderId);
        if (folder.isOnline() == false) {
            return;
        }
        folder.setOnline(false);
        getFileApi(folder.getSpace()).moveFolderOffline(folder);
    }


    @Override
    public void favoriteFolder(String folderId, UserDetails currentUser) {
        Folder folder = getFolderById(folderId);
        // TODO
    }

    @Override
    public void unfavoriteFolder(String folderId, UserDetails currentUser) {
        Folder folder = getFolderById(folderId);
        // TODO
    }

    @Override
    public void shareFolder(String folderId, List<Long> userIds, UserDetails currentUser) {
        Folder folder = getFolderById(folderId);
        // TODO
    }

    @Override
    public List<Folder> getFavorites(String spaceId, UserDetails currentUser) {
        return null;
    }

    private FileApiAdapter getFileApi(String type) {
        return new FileApiAdapter(fileRepositoryService.getOfType(type));
    }
}
