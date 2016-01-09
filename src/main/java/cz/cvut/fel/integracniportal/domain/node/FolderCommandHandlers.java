package cz.cvut.fel.integracniportal.domain.node;

import cz.cvut.fel.integracniportal.command.node.*;
import cz.cvut.fel.integracniportal.exceptions.IllegalOperationException;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Radek Jezdik
 */
@Component
public class FolderCommandHandlers extends AbstractNodeCommandHandler {

    @Resource(name = "folderAggregateRepository")
    private Repository<Folder> repository;

    @CommandHandler
    public void handle(CreateFolderCommand command) {
        Folder folder = new Folder(
                command.getId(),
                command.getName(),
                command.getParentFolder(),
                command.getOwner(),
                command.getSpace());

        checkUniqueName(folder.getName(), folder.getParentFolder(), folder.getSpace(), command);

        repository.add(folder);
    }

    @CommandHandler
    public void handle(RenameFolderCommand command) {
        Folder folder = repository.load(command.getId());

        if (folder.getName().equals(command.getNewName())) {
            return;
        }

        checkUniqueName(command.getNewName(), folder.getParentFolder(), folder.getSpace(), command);

        folder.renameFolder(command.getNewName());
    }

    @CommandHandler
    public void handle(MoveFolderCommand command) {
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

        checkUniqueName(folder.getName(), command.getNewParent(), folder.getSpace(), command);

        folder.moveFolder(command.getNewParent(), command.getSentBy());
    }

    @CommandHandler
    public void handle(DeleteFolderCommand command) {
        Folder folder = repository.load(command.getId());
        folder.recursivelyDelete();
    }

    @CommandHandler
    public void handle(DeleteFolderInternalCommand command) {
        Folder folder = repository.load(command.getId());
        folder.delete();
    }

    @CommandHandler
    public void handle(MoveFolderOnlineCommand command) {
        Folder folder = repository.load(command.getId());
        folder.moveOnline();
    }

    @CommandHandler
    public void handle(MoveFolderOfflineCommand command) {
        Folder folder = repository.load(command.getId());
        folder.moveOffline();
    }

    public void setRepository(Repository<Folder> repository) {
        this.repository = repository;
    }

}
