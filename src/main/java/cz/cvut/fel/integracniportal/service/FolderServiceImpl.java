package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.representation.FolderRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link cz.cvut.fel.integracniportal.service.FolderService}.
 */
@Service
public class FolderServiceImpl implements FolderService {

    @Autowired
    private FolderDao folderDao;

    @Override
    public Folder getFolderById(long id) {
        return folderDao.getFolderById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public FolderRepresentation getFolderRepresentationById(long id) {
        Folder folder = getFolderById(id);
        if (folder == null) {
            return null;
        }
        FolderRepresentation folderRepresentation = new FolderRepresentation(folder);
        return folderRepresentation;
    }

    @Override
    public List<Folder> getTopLevelFolders() {
        return folderDao.getTopLevelFolders();
    }

    @Override
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
    public Folder createFolder(Folder folder) {
        folderDao.createFolder(folder);
        return folder;
    }

    @Override
    public Folder createFolder(String folderName, Folder parent) {
        Folder newFolder = new Folder();
        newFolder.setName(folderName);
        newFolder.setParent(parent);
        createFolder(newFolder);
        return newFolder;
    }

    @Override
    public Folder updateFolder(Folder folder) {
        folderDao.updateFolder(folder);
        return folder;
    }

    @Override
    public void removeFolder(Folder folder) {
        folderDao.removeFolder(folder);
    }
}
