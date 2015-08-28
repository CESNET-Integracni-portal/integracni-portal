package cz.cvut.fel.integracniportal.domain.node;

import cz.cvut.fel.integracniportal.command.node.CreateFolderCommand;
import cz.cvut.fel.integracniportal.command.node.MoveFolderCommand;
import cz.cvut.fel.integracniportal.command.node.RenameFolderCommand;
import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.exceptions.IllegalOperationException;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Radek Jezdik
 */
@Component
public class FolderCommandHandlers extends AbstractNodeCommandHandler {

    @Resource(name = "folderAggregateRepository")
    private Repository<Folder> repository;

    @Autowired
    private FolderDao dao;

    @CommandHandler
    public void handle(CreateFolderCommand command) {
        createFolder(command);
    }

    @CommandHandler
    public void handle(RenameFolderCommand command) {
        renameFolder(command);
    }

    @CommandHandler
    public void handle(MoveFolderCommand command) {
        moveFolder(command);
    }

    private void createFolder(CreateFolderCommand command) {
        Folder folder = new Folder(
                command.getId(),
                command.getName(),
                command.getParentFolder(),
                command.getOwner(),
                command.getSpace());

        checkUniqueName(folder.getName(), folder.getParentFolder(), folder, command);

        repository.add(folder);
    }

    private void renameFolder(RenameFolderCommand command) {
        Folder folderAggregate = repository.load(command.getId());

        if (folderAggregate.getName().equals(command.getNewName())) {
            return;
        }

        checkUniqueName(command.getNewName(), folderAggregate.getParentFolder(), folderAggregate, command);

        folderAggregate.renameFolder(command.getNewName());
    }

    private void moveFolder(MoveFolderCommand command) {
        Folder folder = repository.load(command.getId());

        if (folder.getId().equals(command.getNewParent())) {
            throw new IllegalOperationException("Cannot move folder to itself");
        }
        if (folder.getParentFolder() == null && command.getNewParent() == null) {
            return;
        }
        if (folder.getParentFolder() != null && folder.getParentFolder().equals(command.getNewParent())) {
            return;
        }

        checkUniqueName(folder.getName(), command.getNewParent(), folder, command);

        folder.moveFolder(command.getNewParent(), command.getSentBy());
    }

    public void setRepository(Repository<Folder> repository) {
        this.repository = repository;
    }

}
