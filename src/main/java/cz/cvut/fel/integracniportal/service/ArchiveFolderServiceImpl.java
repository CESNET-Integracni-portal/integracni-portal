package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.FolderRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link ArchiveFolderService}.
 */
@Service
public class ArchiveFolderServiceImpl implements ArchiveFolderService {

    @Autowired
    private FolderDao folderDao;

    @Autowired
    private ArchiveFileMetadataService archiveFileMetadataService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Folder getFolderById(long id) throws NotFoundException {
        Folder folder = folderDao.get(id);
        if (folder == null) {
            throw new NotFoundException("cesnet.folder.notFound", id);
        }
        return folder;
    }

    @Override
    @Transactional(readOnly = true)
    public FolderRepresentation getFolderRepresentationById(long id) throws NotFoundException {
        Folder folder = getFolderById(id);
        FolderRepresentation folderRepresentation = new FolderRepresentation(folder);
        return folderRepresentation;
    }

    @Override
    public List<Folder> getTopLevelFolders() {
        return folderDao.getTopLevelFolders();
    }

    @Override
    @Transactional
    public List<FolderRepresentation> getTopLevelFolderRepresentations() {
        List<Folder> folders = getTopLevelFolders();
        List<FolderRepresentation> folderResources = new ArrayList<FolderRepresentation>(folders.size());
        for (Folder folder: folders) {
            FolderRepresentation folderResource = new FolderRepresentation(folder, false);
            folderResources.add(folderResource);
        }
        return folderResources;
    }

    @Override
    @Transactional
    public Folder createFolder(Folder folder) {
        UserDetails currentUser = userDetailsService.getCurrentUser();
        folder.setOwner(currentUser);
        folderDao.createFolder(folder);
        return folder;
    }

    @Override
    public Folder createTopLevelFolder(String folderName) {
        Folder newFolder = new Folder();
        newFolder.setName(folderName);
        createFolder(newFolder);
        return newFolder;
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    public Folder createSubFolder(String folderName, Long parentId) throws NotFoundException {
        Folder parent = getFolderById(parentId);
        return createSubFolder(folderName, parent);
    }

    @Override
    public Folder createSubFolder(String folderName, Folder parent) {
        Folder newFolder = new Folder();
        newFolder.setName(folderName);
        newFolder.setParent(parent);
        createFolder(newFolder);
        return newFolder;
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    public Folder updateFolder(Long folderId, FolderRepresentation folderRepresentation) throws NotFoundException {
        Folder folder = getFolderById(folderId);
        folder.setName(folderRepresentation.getName());
        return updateFolder(folder);
    }

    @Override
    public Folder updateFolder(Folder folder) {
        folderDao.update(folder);
        return folder;
    }

    @Override
    @Transactional(rollbackFor = ServiceAccessException.class)
    public void removeFolder(Long folderId) throws ServiceAccessException, NotFoundException {
        Folder folder = getFolderById(folderId);
        removeFolder(folder);
    }

    @Override
    @Transactional(rollbackFor = ServiceAccessException.class)
    public void removeFolder(Folder folder) throws ServiceAccessException {
        for (FileMetadata fileMetadata: folder.getFiles()) {
            archiveFileMetadataService.deleteFile(fileMetadata);
        }
        for (Folder subFolder: folder.getFolders()) {
            removeFolder(subFolder);
        }
        folderDao.delete(folder);
    }
}
