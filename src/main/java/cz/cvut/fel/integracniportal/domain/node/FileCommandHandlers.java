package cz.cvut.fel.integracniportal.domain.node;

import cz.cvut.fel.integracniportal.command.node.*;
import cz.cvut.fel.integracniportal.dao.FileMetadataDao;
import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileState;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.service.FileApiAdapter;
import cz.cvut.fel.integracniportal.service.FileUpload;
import cz.cvut.fel.integracniportal.service.SpaceServiceImpl;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.axonframework.unitofwork.UnitOfWork;
import org.axonframework.unitofwork.UnitOfWorkListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Radek Jezdik
 */
@Component
public class FileCommandHandlers extends AbstractNodeCommandHandler {

    @Resource(name = "fileAggregateRepository")
    private Repository<File> repository;

    @Autowired
    private SpaceServiceImpl fileRepositoryService;

    @Autowired
    private FolderDao folderDao;

    @Autowired
    private UserDetailsDao userDao;

    @Autowired
    private FileMetadataDao fileDao;

    @CommandHandler
    public void handle(CreateFileCommand command, UnitOfWork unitOfWork) {
        createFile(command, unitOfWork);
    }

    @CommandHandler
    public void handle(RenameFileCommand command) {
        renameFile(command);
    }

    @CommandHandler
    public void handle(MoveFileCommand command) {
        moveFile(command);
    }

    @CommandHandler
    public void handle(DeleteFileCommand command, UnitOfWork unitOfWork) {
        deleteFile(command, unitOfWork);
    }

    @CommandHandler
    public void handle(UpdateFileContentsCommand command) {
        updateFileContents(command);
    }

    @CommandHandler
    public void handle(DownloadFileCommand command) {
        File file = repository.load(command.getId());
        file.download();
    }

    @CommandHandler
    public void moveFileOffline(MoveFileOfflineCommand command, UnitOfWork unitOfWork) {
        File file = repository.load(command.getId());
        file.moveOffline();

        FileMetadata fileMetadata = fileDao.getByUUID(command.getId().getId());
        getFileApi(file.getSpace()).moveFileOffline(fileMetadata);
    }

    @CommandHandler
    public void moveFileOnline(MoveFileOnlineCommand command, UnitOfWork unitOfWork) {
        File file = repository.load(command.getId());
        file.moveOnline();

        FileMetadata fileMetadata = fileDao.getByUUID(command.getId().getId());
        getFileApi(file.getSpace()).moveFileOnline(fileMetadata);
    }

    private void createFile(final CreateFileCommand command, UnitOfWork unitOfWork) {
        checkUniqueName(command.getName(), command.getParentFolder(), command.getSpace(), command);

        FileUpload fileUpload = command.getFileUpload();

        sendFileToRepository(fileUpload, command, unitOfWork);

        File file = new File(
                command.getId(),
                command.getName(),
                command.getParentFolder(),
                command.getOwner(),
                command.getSpace(),
                fileUpload.getByteReadCount(),
                fileUpload.getContentType(),
                command.getFileState().orElse(FileState.ONLINE));

        repository.add(file);
    }

    private void sendFileToRepository(FileUpload fileUpload, final CreateFileCommand command, UnitOfWork unitOfWork) {
        final FileMetadata fileMetadata = new FileMetadata();

        fileMetadata.setId(command.getId().getId());
        fileMetadata.setName(command.getName());
        if (command.getParentFolder() != null) {
            fileMetadata.setParent(folderDao.get(command.getParentFolder().getId()));
        }
        fileMetadata.setMimetype(fileUpload.getContentType());
        fileMetadata.setSpace(command.getSpace());
        fileMetadata.setOnline(true);
        fileMetadata.setOwner(userDao.getUserById(command.getSentBy().getId()));

        getFileApi(command.getSpace()).putFile(fileMetadata, fileUpload.getInputStream());

        fileMetadata.setFilesize(fileUpload.getByteReadCount());

        // undo the file upload into the repository on exception
        unitOfWork.registerListener(new UnitOfWorkListenerAdapter() {

            @Override
            public void onRollback(UnitOfWork unitOfWork, Throwable failureCause) {
                getFileApi(command.getSpace()).deleteFile(fileMetadata);
            }

        });
    }

    private void renameFile(RenameFileCommand command) {
        File file = repository.load(command.getId());

        if (file.getName().equals(command.getNewName())) {
            return;
        }

        checkUniqueName(command.getNewName(), file.getParentFolder(), file.getSpace(), command);

        file.rename(command.getNewName());

        getFileApi(file.getSpace()).renameFile(fileDao.getByUUID(file.getId().getId()), command.getNewName());
    }

    private void moveFile(MoveFileCommand command) {
        File file = repository.load(command.getId());

        if (file.getParentFolder() == null && command.getNewParent() == null) {
            return;
        }
        if (file.getParentFolder() != null && file.getParentFolder().equals(command.getNewParent())) {
            return;
        }

        checkUniqueName(file.getName(), command.getNewParent(), file.getSpace(), command);

        file.move(command.getNewParent(), command.getSentBy());

        cz.cvut.fel.integracniportal.model.Folder parent = null;
        if (command.getNewParent() != null) {
            parent = folderDao.get(command.getNewParent().getId());
        }
        getFileApi(file.getSpace()).moveFile(fileDao.getByUUID(file.getId().getId()), parent);
    }

    private void deleteFile(DeleteFileCommand command, UnitOfWork unitOfWork) {
        final File file = repository.load(command.getId());

        final FileMetadata fileMetadata = fileDao.getByUUID(command.getId().getId());

        unitOfWork.registerListener(new UnitOfWorkListenerAdapter() {
            @Override
            public void afterCommit(UnitOfWork unitOfWork) {
                getFileApi(fileMetadata.getSpace()).deleteFile(fileMetadata);
            }
        });

        file.delete();
    }

    private void updateFileContents(UpdateFileContentsCommand command) {
        File file = repository.load(command.getId());

        FileUpload fileUpload = command.getFileUpload();
        FileMetadata fileMetadata = fileDao.getByUUID(command.getId().getId());

        getFileApi(file.getSpace()).putFile(fileMetadata, fileUpload.getInputStream());

        file.updateContents(fileUpload.getByteReadCount());
    }

    public void setRepository(Repository<File> repository) {
        this.repository = repository;
    }

    private FileApiAdapter getFileApi(String type) {
        return new FileApiAdapter(fileRepositoryService.getOfType(type));
    }

}
