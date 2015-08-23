package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.command.node.CreateFileCommand;
import cz.cvut.fel.integracniportal.dao.FileMetadataDao;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileState;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.exceptions.FileIOException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link FileMetadataService}.
 */
@Service
public class FileMetadataServiceImpl implements FileMetadataService {

    @Autowired
    private FileMetadataDao fileMetadataDao;

    @Autowired
    private FolderService folderService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SpaceServiceImpl fileRepositoryService;

    @Autowired
    private CommandGateway gateway;

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
    public FileMetadata uploadFileToRoot(String space, MultipartFile file) {
        return uploadFile(null, space, file);
    }

    @Override
    public FileMetadata uploadFileToFolder(String parentFolderId, String space, MultipartFile file) {
        Folder folder = folderService.getFolderById(parentFolderId);

        if (folder.getSpace().equals(space) == false) {
            throw new NotFoundException("Folder not found in space");
        }

        FolderId folderId = FolderId.of(folder.getId());

        return uploadFile(folderId, space, file);
    }

    public FileMetadata uploadFile(FolderId folderId, String space, MultipartFile file) {
        UserDetails currentUser = userDetailsService.getCurrentUser();

        String id = UUID.randomUUID().toString();

        gateway.sendAndWait(new CreateFileCommand(
                FileId.of(id),
                file.getOriginalFilename(),
                folderId,
                UserId.of(currentUser.getId()),
                space,
                file.getSize(),
                file.getContentType(),
                Optional.of(FileState.ONLINE)
        ));

        FileMetadata fileMetadata = fileMetadataDao.getByUUID(id);

        try {
            getFileApi(space).putFile(fileMetadata, file.getInputStream());

            return fileMetadata;
        } catch (IOException e) {
            throw new FileIOException("Could not read uploaded file", e, "cesnet.service.unavailable");
        }
    }

    @Override
    public void updateFile(String fileuuid, MultipartFile file) {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileuuid);
        try {
            getFileApi(fileMetadata.getSpace()).putFile(fileMetadata, file.getInputStream());
            setFileMetadata(fileMetadata, file);
            updateFileMetadata(fileMetadata);
        } catch (IOException e) {
            throw new FileIOException("Could not read uploaded file", e, "cesnet.service.unavailable");
        }
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
    public void moveFile(String fileId, String parentId) {
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
            getFileApi(fileMetadata.getSpace()).moveFileToBin(fileMetadata);
        }
    }

    @Override
    public InputStream getFileAsInputStream(String fileuuid) {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileuuid);
        String space = fileMetadata.getSpace();

        return getFileApi(space).getFile(fileMetadata);
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

    private void setFileMetadata(FileMetadata fileMetadata, MultipartFile file) {
        fileMetadata.setFilename(file.getOriginalFilename());
        fileMetadata.setMimetype(file.getContentType());
        fileMetadata.setFilesize(file.getSize());
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

    private FileApiAdapter getFileApi(String type) {
        return new FileApiAdapter(fileRepositoryService.getOfType(type));
    }
}
