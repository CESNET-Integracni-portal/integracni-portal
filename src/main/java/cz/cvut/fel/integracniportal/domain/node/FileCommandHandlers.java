package cz.cvut.fel.integracniportal.domain.node;

import cz.cvut.fel.integracniportal.command.node.*;
import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.dao.NodeNameDao;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileState;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Radek Jezdik
 */
@Component
public class FileCommandHandlers {

    @Resource(name = "fileAggregateRepository")
    private Repository<File> repository;

    @Autowired
    private FolderDao folderDao;

    @Autowired
    private NodeNameDao nodeNameDao;

    @CommandHandler
    public void handle(CreateFileCommand command) {
        createFile(command);
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
    public void moveFileOffline(MoveFileOfflineCommand command) {
        File file = repository.load(command.getId());
        file.moveFileOffline();
    }

    @CommandHandler
    public void moveFileOnline(MoveFileOnlineCommand command) {
        File file = repository.load(command.getId());
        file.moveFileOnline();
    }

    private void createFile(CreateFileCommand command) {
        File file = new File(
                command.getId(),
                command.getName(),
                command.getParentFolder(),
                command.getOwner(),
                command.getSpace(),
                command.getSize(),
                command.getMimetype(),
                command.getFileState().orElse(FileState.ONLINE));

        checkUniqueName(file.getName(), file.getParentFolder(), file, command);

        repository.add(file);
    }

    private void renameFile(RenameFileCommand command) {
        File file = repository.load(command.getId());

        if (file.getName().equals(command.getNewName())) {
            return;
        }

        checkUniqueName(command.getNewName(), file.getParentFolder(), file, command);

        file.renameFile(command.getNewName());
    }

    private void moveFile(MoveFileCommand command) {
        File file = repository.load(command.getId());

        if (file.getParentFolder() == null && command.getNewParent() == null) {
            return;
        }
        if (file.getParentFolder() != null && file.getParentFolder().equals(command.getNewParent())) {
            return;
        }

        checkUniqueName(file.getName(), command.getNewParent(), file, command);

        file.moveFile(command.getNewParent(), command.getSentBy());
    }

    private void checkUniqueName(String name, FolderId parentFolder, File file, UserAwareCommand command) {
        boolean exists;

        if (parentFolder == null) {
            exists = nodeNameDao.nameInRootExists(name, command.getSentBy(), file.getSpace());
        } else {
            exists = nodeNameDao.nameExists(name, parentFolder);
        }

        if (exists) {
            throw new DuplicateNameException();
        }
    }

    public void setRepository(Repository<File> repository) {
        this.repository = repository;
    }

}
