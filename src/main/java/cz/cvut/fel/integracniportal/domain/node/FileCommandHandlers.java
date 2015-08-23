package cz.cvut.fel.integracniportal.domain.node;

import cz.cvut.fel.integracniportal.command.node.*;
import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileState;
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Radek Jezdik
 */
@Component
public class FileCommandHandlers {

    @Resource(name = "fileAggregateRepository")
    private Repository<File> repository;

    @Autowired
    private FolderDao dao;

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
        repository.add(file);
    }

    private void renameFile(RenameFileCommand command) {
        File file = repository.load(command.getId());

        if (file.getName().equals(command.getNewName())) {
            return;
        }

        Folder parent = dao.get(file.getParentFolder().getId());

        boolean fileNameExists = fileNameExistsInFolder(parent, command.getNewName());

        if (fileNameExists == false) {
            file.renameFile(command.getNewName());
        } else {
            throw new DuplicateNameException();
        }

    }

    private void moveFile(MoveFileCommand command) {
        File file = repository.load(command.getId());

        if (file.getParentFolder().equals(command.getNewParent())) {
            return;
        }

        Folder newParentFolder = dao.get(command.getNewParent().getId());

        boolean fileNameExists = fileNameExistsInFolder(newParentFolder, file.getName());

        if (fileNameExists == false) {
            file.moveFile(command.getNewParent());
        } else {
            throw new DuplicateNameException();
        }
    }

    private boolean fileNameExistsInFolder(Folder newParentFolder, String name) {
        List<FileMetadata> files = newParentFolder.getFiles();

        if (files == null) {
            return false;
        }

        for (FileMetadata f : files) {
            if (f.getFilename().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @CommandHandler
    public void moveFileOnline(MoveFileOnlineCommand command) {
        File file = repository.load(command.getId());
        file.moveFileOnline();
    }

    public void setRepository(Repository<File> repository) {
        this.repository = repository;
    }
}
