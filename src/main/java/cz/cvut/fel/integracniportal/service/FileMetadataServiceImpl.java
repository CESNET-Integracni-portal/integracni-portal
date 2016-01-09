package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.command.node.*;
import cz.cvut.fel.integracniportal.dao.FileMetadataDao;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileState;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
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
    public List<FileMetadata> getFilesByLabels(String spaceId, List<String> labelIds, UserDetails owner) {
        return fileMetadataDao.getFilesByLabels(spaceId, labelIds, owner);
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
    @Transactional(readOnly = true)
    public List<FileMetadata> getOldFilesForDeletion() {
        return fileMetadataDao.getFilesForDeletion();
    }

    @Override
    public FileMetadata uploadFileToRoot(String space, FileUpload file) {
        return uploadFile(null, space, file);
    }

    @Override
    public FileMetadata uploadFileToFolder(String parentFolderId, String space, FileUpload file) {
        Folder folder = folderService.getFolderById(parentFolderId);

        if (folder.getSpace().equals(space) == false) {
            throw new NotFoundException("Folder not found in space");
        }

        FolderId folderId = FolderId.of(folder.getId());

        return uploadFile(folderId, space, file);
    }

    private FileMetadata uploadFile(FolderId folderId, String space, FileUpload file) {
        UserDetails currentUser = userDetailsService.getCurrentUser();

        String id = UUID.randomUUID().toString();

        gateway.sendAndWait(new CreateFileCommand(
                FileId.of(id),
                file,
                folderId,
                UserId.of(currentUser.getId()),
                space,
                Optional.of(FileState.ONLINE)
        ));

        return fileMetadataDao.getByUUID(id);
    }

    @Override
    public void updateFile(String fileId, FileUpload fileUpload) {
        gateway.sendAndWait(new UpdateFileContentsCommand(
                FileId.of(fileId),
                fileUpload
        ));
    }

    @Override
    public void renameFile(String fileId, String name) {
        gateway.sendAndWait(new RenameFileCommand(
                FileId.of(fileId),
                name
        ));
    }

    @Override
    public void moveFile(String fileId, String parentId) {
        gateway.sendAndWait(new MoveFileCommand(
                FileId.of(fileId),
                FolderId.of(parentId)
        ));
    }

    @Override
    public void deleteFile(String uuid) {
        gateway.sendAndWait(new DeleteFileCommand(
                FileId.of(uuid)
        ));
    }

    @Override
    public void downloadFile(String id, OutputStream outputStream) {
        gateway.send(new DownloadFileCommand(FileId.of(id)));

        FileMetadata fileMetadata = getFileMetadataByUuid(id);
        String space = fileMetadata.getSpace();

        getFileApi(space).getFile(fileMetadata, outputStream);
    }

    @Override
    public void moveFileOnline(String fileId) {
        gateway.sendAndWait(new MoveFileOnlineCommand(
                FileId.of(fileId)
        ));
    }

    @Override
    public void moveFileOffline(String fileId) {
        gateway.sendAndWait(new MoveFileOfflineCommand(
                FileId.of(fileId)
        ));
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
