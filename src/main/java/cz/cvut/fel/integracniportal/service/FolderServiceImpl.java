package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.exceptions.InvalidStateException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.FolderRepresentation;
import cz.cvut.fel.integracniportal.representation.TopLevelFolderRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Radek Jezdik
 */
@Service
@Transactional
public class FolderServiceImpl implements FolderService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private FolderDao folderDao;

    @Autowired
    private SpaceServiceImpl fileRepositoryService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private AclService aclService;

    @Override
    @Transactional(readOnly = true)
    public Folder getFolderById(long id) {
        Folder folder = folderDao.get(id);
        if (folder == null) {
            throw new NotFoundException("cesnet.folder.notFound", id);
        }
        return folder;
    }

    @Override
    @Transactional(readOnly = true)
    public FolderRepresentation getFolderRepresentationById(long id, UserDetails currentUser) {
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
        TopLevelFolderRepresentation representation = new TopLevelFolderRepresentation(topLevelFolders, topLevelFiles, owner);
        return representation;
    }

    @Override
    public TopLevelFolderRepresentation getTopLevelFolderByLabels(String spaceId, List<Long> labels, UserDetails owner) {
        List<Folder> folders = folderDao.getFoldersByLabels(spaceId, labels, owner);
        List<FileMetadata> files = fileMetadataService.getFilesByLabels(spaceId, labels, owner);
        TopLevelFolderRepresentation representation = new TopLevelFolderRepresentation(folders, files, owner);
        return representation;
    }

    @Override
    public Folder createFolder(Folder folder, UserDetails owner) {
        FileApiAdapter fileApi = getFileApi(folder.getSpace());

        folder.setOwner(owner);

        folderDao.createFolder(folder);
        fileApi.createFolder(folder);

        return folder;
    }

    @Override
    public Folder createTopLevelFolder(String folderName, String spaceId, UserDetails owner) {
        Folder folder = new Folder();
        folder.setName(folderName);
        folder.setSpace(spaceId);
        folder.setAcParent(aclService.getAceParent(null));

        return createFolder(folder, owner);
    }

    @Override
    public Folder createSubFolder(String folderName, Long parentId, UserDetails owner) {
        Folder parent = getFolderById(parentId);
        return createSubFolder(folderName, parent, owner);
    }

    @Override
    public Folder createSubFolder(String folderName, Folder parent, UserDetails owner) {
        String space = parent.getSpace();

        Folder newFolder = new Folder();
        newFolder.setName(folderName);
        newFolder.setParent(parent);
        newFolder.setSpace(space);
        newFolder.setOwner(owner);
        newFolder.setAcParent(aclService.getAceParent(parent));

        createFolder(newFolder, owner);

        return newFolder;
    }

    @Override
    public Folder renameFolder(Long folderId, String newName) {
        Folder folder = getFolderById(folderId);

        getFileApi(folder.getSpace()).renameFolder(folder, newName);

        folder.setName(newName);
        folderDao.update(folder);

        return folder;
    }

    @Override
    public void removeFolder(Long folderId) {
        Folder folder = getFolderById(folderId);
        removeFolder(folder, true);
    }

    private void removeFolder(Folder folder, boolean removeFromRepository) {

        for (Folder subFolder : folder.getFolders()) {
            removeFolder(subFolder, false);
        }
        for (FileMetadata fileMetadata : folder.getFiles()) {
            fileMetadataService.deleteFile(fileMetadata, false);
        }
        if (removeFromRepository) {
            getFileApi(folder.getSpace()).moveFolderToBin(folder);
        }
        folderDao.delete(folder);
    }

    @Override
    public void updateFolder(Folder folder) {
        folderDao.update(folder);
    }

    @Override
    public void moveFolder(Long folderId, Long parentId) {
        Folder folder = getFolderById(folderId);
        Folder parent = parentId == null ? null : getFolderById(parentId);

        if (folder.equals(parent)) {
            throw new InvalidStateException("Can not move folder to itself");
        }

        //Folder is moved within space root or the same parent -> ignore
        if ((parentId == null && folder.getParent() == null) || (folder.getParent() != null && folder.getParent().equals(parent))) {
            return;
        }

        aclService.updateAceParent(folder, parent);

        folder.setParent(parent);

        updateFolder(folder);
        getFileApi(folder.getSpace()).moveFolder(folder, parent);
    }

    @Override
    public void moveFolderOnline(Long folderId) {
        Folder folder = getFolderById(folderId);
        if (folder.isOnline()) {
            return;
        }
        folder.setOnline(true);
        getFileApi(folder.getSpace()).moveFolderOnline(folder);

    }

    @Override
    public void moveFolderOffline(Long folderId) {
        Folder folder = getFolderById(folderId);
        if (folder.isOnline() == false) {
            return;
        }
        folder.setOnline(false);
        getFileApi(folder.getSpace()).moveFolderOffline(folder);
    }


    @Override
    public void favoriteFolder(Long folderId, UserDetails currentUser) {
        Folder folder = getFolderById(folderId);
        // TODO
    }

    @Override
    public void unfavoriteFolder(Long folderId, UserDetails currentUser) {
        Folder folder = getFolderById(folderId);
        // TODO
    }

    @Override
    public void shareFolder(Long folderId, List<Long> userIds, UserDetails currentUser) {
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
