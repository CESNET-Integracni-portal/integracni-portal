package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
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
@Transactional
public class ArchiveFolderServiceImpl implements ArchiveFolderService {

    @Autowired
    private FolderDao folderDao;

    @Autowired
    private ArchiveFileMetadataService archiveFileMetadataService;

    @Autowired
    private UserDetailsService userDetailsService;

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
    public FolderRepresentation getFolderRepresentationById(long id) {
        Folder folder = getFolderById(id);
        return new FolderRepresentation(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> getTopLevelFolders() {
        return folderDao.getTopLevelFolders();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderRepresentation> getTopLevelFolderRepresentations() {
        List<Folder> folders = getTopLevelFolders();
        List<FolderRepresentation> folderResources = new ArrayList<FolderRepresentation>(folders.size());
        for (Folder folder : folders) {
            FolderRepresentation folderResource = new FolderRepresentation(folder, false);
            folderResources.add(folderResource);
        }
        return folderResources;
    }

    @Override
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
    public Folder createSubFolder(String folderName, Long parentId) {
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
    public Folder updateFolder(Long folderId, FolderRepresentation folderRepresentation) {
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
    public void removeFolder(Long folderId) {
        Folder folder = getFolderById(folderId);
        removeFolder(folder);
    }

    @Override
    public void removeFolder(Folder folder) {
        for (FileMetadata fileMetadata : folder.getFiles()) {
            archiveFileMetadataService.deleteFile(fileMetadata);
        }
        for (Folder subFolder : folder.getFolders()) {
            removeFolder(subFolder);
        }
        folderDao.delete(folder);
    }

}
