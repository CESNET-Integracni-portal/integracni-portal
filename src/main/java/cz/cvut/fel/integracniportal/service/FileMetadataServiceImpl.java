package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AclPermissionDao;
import cz.cvut.fel.integracniportal.dao.FileMetadataDao;
import cz.cvut.fel.integracniportal.model.*;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.OutputStream;
import java.security.acl.Acl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the {@link FileMetadataService}.
 */
@Service
@Transactional
public class FileMetadataServiceImpl implements FileMetadataService {

    private static final Logger logger = Logger.getLogger(FileMetadataServiceImpl.class);

    @Autowired
    private FileMetadataDao fileMetadataDao;

    @Autowired
    private FolderService folderService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SpaceServiceImpl fileRepositoryService;

    @Autowired
    private AclService aclService;

    @Override
    @Transactional(readOnly = true)
    public List<FileMetadata> getTopLevelFiles(String spaceId, UserDetails owner) {
        getFileApi(spaceId); // ensures existing space
        return fileMetadataDao.getAllTopLevelFiles(spaceId, owner);
    }

    @Override
    public List<FileMetadata> getFilesByLabels(String spaceId, List<Long> labels, UserDetails owner) {
        return fileMetadataDao.getFilesByLabels(spaceId, labels, owner);
    }

    @Override
    @Transactional(readOnly = true)
    public FileMetadata getFileMetadataByUuid(String fileMetadataUuid) {
        return fileMetadataDao.getByUUID(fileMetadataUuid);
    }

    @Override
    public FileMetadataRepresentation getFileMetadataRepresentationByUuid(String fileId) {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileId);
        return new FileMetadataRepresentation(fileMetadata);
    }

    @Override
    public void createFileMetadata(FileMetadata fileMetadata) {
        fileMetadataDao.createFileMetadata(fileMetadata);
    }

    @Override
    public void updateFileMetadata(FileMetadata fileMetadata) {
        fileMetadataDao.update(fileMetadata);
    }

    @Override
    public void removeFileMetadata(FileMetadata fileMetadata) {
        fileMetadataDao.delete(fileMetadata);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileMetadata> getOldFilesForDeletion() {
        return fileMetadataDao.getFilesForDeletion();
    }

    @Override
    public FileMetadata uploadFileToRoot(String space, FileUpload fileUpload) {
        return upload(space, null, fileUpload);
    }

    @Override
    public FileMetadata uploadFileToFolder(Long parentFolderId, FileUpload fileUpload) {
        Folder parent = folderService.getFolderById(parentFolderId);
        return upload(parent.getSpace(), parent, fileUpload);
    }

    private FileMetadata upload(String space, Folder parent, FileUpload fileUpload) {
        FileMetadata fileMetadata = new FileMetadata();

        UserDetails currentUser = userDetailsService.getCurrentUser();

        fileMetadata.setFilename(fileUpload.getFileName());
        fileMetadata.setMimetype(fileUpload.getContentType());
        fileMetadata.setParent(parent);
        fileMetadata.setOwner(currentUser);
        fileMetadata.setSpace(space);
        fileMetadata.setFilesize(0L);

        createFileMetadata(fileMetadata);

        getFileApi(space).putFile(fileMetadata, fileUpload.getInputStream());

        fileMetadata.setFilesize(fileUpload.getByteReadCount());
        updateFileMetadata(fileMetadata);

        return fileMetadata;
    }

    @Override
    public void updateFile(String fileuuid, FileUpload fileUpload) {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileuuid);

        getFileApi(fileMetadata.getSpace()).putFile(fileMetadata, fileUpload.getInputStream());

        fileMetadata.setFilesize(fileUpload.getByteReadCount());
        fileMetadata.setMimetype(fileUpload.getContentType());

        updateFileMetadata(fileMetadata);
    }

    @Override
    public void renameFile(String fileId, String name) {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileId);
        fileMetadata.setFilename(name);
        updateFileMetadata(fileMetadata);

        // TODO: bug #6 (calling api with already renamed file metadata)
        getFileApi(fileMetadata.getSpace()).renameFile(fileMetadata, name);
    }

    @Override
    public void moveFile(String fileId, Long parentId) {
        FileMetadata file = getFileMetadataByUuid(fileId);
        Folder parent = folderService.getFolderById(parentId);

        if (file.getParent() != null && file.getParent().equals(parent)) {
            return; // same parent, file not moved
        }

        file.setParent(parent);

        updateFileMetadata(file);
        getFileApi(file.getSpace()).moveFile(file, parent);
    }

    @Override
    public void deleteFile(String uuid) {
        FileMetadata fileMetadata = getFileMetadataByUuid(uuid);
        deleteFile(fileMetadata, true);
    }

    @Override
    public void deleteFile(FileMetadata fileMetadata, boolean removeFromRepository) {
        removeFileMetadata(fileMetadata);
        if (removeFromRepository) {
            getFileApi(fileMetadata.getSpace()).deleteFile(fileMetadata);
        }
    }

    @Override
    public void copyFileToOutputStream(String fileuuid, OutputStream outputStream) {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileuuid);
        String space = fileMetadata.getSpace();

        getFileApi(space).getFile(fileMetadata, outputStream);
    }

    @Override
    public void moveFileOnline(String fileId) {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileId);
        if (fileMetadata.isOnline()) {
            return;
        }
        fileMetadata.setOnline(true);
        getFileApi(fileMetadata.getSpace()).moveFileOnline(fileMetadata);
    }

    @Override
    public void moveFileOffline(String fileId) {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileId);
        if (fileMetadata.isOnline() == false) {
            return;
        }
        fileMetadata.setOnline(false);
        getFileApi(fileMetadata.getSpace()).moveFileOffline(fileMetadata);
    }

    @Override
    public void favoriteFile(String fileId, UserDetails currentUser) {
        FileMetadata file = getFileMetadataByUuid(fileId);
        // TODO
    }

    @Override
    public void unfavoriteFile(String fileId, UserDetails currentUser) {
        FileMetadata file = getFileMetadataByUuid(fileId);
        // TODO
    }

    @Override
    public void shareFile(String fileId, List<Long> userIds, UserDetails currentUser) {
        FileMetadata file = getFileMetadataByUuid(fileId);
        // TODO
    }

    @Override
    public void saveFileMetadata(FileMetadata fileMetadata) {
        fileMetadataDao.save(fileMetadata);
    }


    private FileApiAdapter getFileApi(String type) {
        return new FileApiAdapter(fileRepositoryService.getOfType(type));
    }
}