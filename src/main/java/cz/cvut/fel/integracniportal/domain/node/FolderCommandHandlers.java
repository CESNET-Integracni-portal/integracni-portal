package cz.cvut.fel.integracniportal.domain.node;

import cz.cvut.fel.integracniportal.command.node.*;
import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.exceptions.IllegalOperationException;
import cz.cvut.fel.integracniportal.service.FileApiAdapter;
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
public class FolderCommandHandlers extends AbstractNodeCommandHandler {

    @Resource(name = "folderAggregateRepository")
    private Repository<Folder> repository;

    @Autowired
    private SpaceServiceImpl fileRepositoryService;

    @Autowired
    private FolderDao fodlerDao;

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
    public void handle(final RenameFolderCommand command, UnitOfWork unitOfWork) {
        Folder folder = repository.load(command.getId());

        if (folder.getName().equals(command.getNewName())) {
            return;
        }

        checkUniqueName(command.getNewName(), folder.getParentFolder(), folder.getSpace(), command);

        folder.renameFolder(command.getNewName());

        final cz.cvut.fel.integracniportal.model.Folder folderEntity
                = fodlerDao.get(command.getId().getId());

        unitOfWork.registerListener(new UnitOfWorkListenerAdapter() {
            @Override
            public void afterCommit(UnitOfWork unitOfWork) {
                getFileApi(folderEntity.getSpace()).renameFolder(folderEntity, command.getNewName());
            }
        });
    }

    @CommandHandler
    public void handle(MoveFolderCommand command, UnitOfWork unitOfWork) {
        Folder folder = repository.load(command.getId());

        if (folder.getId().equals(command.getNewParent())) {
            throw new IllegalOperationException("Cannot move folder to itself");
        }
        if (folder.getParentFolder() == null && command.getNewParent() == null) {
            return; // no change
        }
        if (folder.getParentFolder() != null && folder.getParentFolder().equals(command.getNewParent())) {
            return; // no change
        }

        checkUniqueName(folder.getName(), command.getNewParent(), folder.getSpace(), command);


        folder.moveFolder(command.getNewParent(), command.getSentBy());


        final cz.cvut.fel.integracniportal.model.Folder folderEntity
                = fodlerDao.get(command.getId().getId());

        final cz.cvut.fel.integracniportal.model.Folder parentFolderEntity
                = command.getNewParent() == null ? null : fodlerDao.get(command.getNewParent().getId());

        unitOfWork.registerListener(new UnitOfWorkListenerAdapter() {
            @Override
            public void afterCommit(UnitOfWork unitOfWork) {
                getFileApi(folderEntity.getSpace()).moveFolder(folderEntity, parentFolderEntity);
            }
        });
    }

    @CommandHandler
    public void handle(DeleteFolderCommand command) {
        Folder folder = repository.load(command.getId());
        folder.recursivelyDelete();
    }

    @CommandHandler
    public void handle(DeleteFolderInternalCommand command, UnitOfWork unitOfWork) {
        Folder folder = repository.load(command.getId());

        final cz.cvut.fel.integracniportal.model.Folder folderEntity
                = fodlerDao.get(command.getId().getId());

        unitOfWork.registerListener(new UnitOfWorkListenerAdapter() {
            @Override
            public void afterCommit(UnitOfWork unitOfWork) {
                getFileApi(folderEntity.getSpace()).deleteFolder(folderEntity);
            }
        });

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

    private FileApiAdapter getFileApi(String type) {
        return new FileApiAdapter(fileRepositoryService.getOfType(type));
    }

    public void setRepository(Repository<Folder> repository) {
        this.repository = repository;
    }

}
