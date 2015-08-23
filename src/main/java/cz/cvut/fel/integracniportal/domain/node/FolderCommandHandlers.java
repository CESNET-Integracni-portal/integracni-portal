package cz.cvut.fel.integracniportal.domain.node;

import cz.cvut.fel.integracniportal.command.node.CreateFolderCommand;
import cz.cvut.fel.integracniportal.command.node.MoveFolderCommand;
import cz.cvut.fel.integracniportal.command.node.RenameFolderCommand;
import cz.cvut.fel.integracniportal.dao.FolderDao;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException;
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
public class FolderCommandHandlers {

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
        FolderId parentFolderId = command.getParentFolder();

        if (parentFolderId != null) {
            cz.cvut.fel.integracniportal.model.Folder parentFolder = dao.get(parentFolderId.getId());


            boolean fileNameExists = fileNameExistsInFolder(parentFolder, command.getName());

            if (fileNameExists) {
                throw new DuplicateNameException();
            }
        } else {
            // TODO duplicate in root folder?
        }

        Folder folder = new Folder(
                command.getId(),
                command.getName(),
                parentFolderId,
                command.getOwner(),
                command.getSpace());
        repository.add(folder);
    }

    private void renameFolder(RenameFolderCommand command) {
        Folder folderAggregate = repository.load(command.getId());

        if (folderAggregate.getName().equals(command.getNewName())) {
            return;
        }

        cz.cvut.fel.integracniportal.model.Folder folder = dao.get(command.getId().getId());
        cz.cvut.fel.integracniportal.model.Folder parentFolder = folder.getParent();

        if (parentFolder != null) {
            boolean fileNameExists = fileNameExistsInFolder(parentFolder, command.getNewName());

            if (fileNameExists) {
                throw new DuplicateNameException();
            }
        } else {
            // TODO duplicate in root folder?
        }

        folderAggregate.renameFolder(command.getNewName());
    }

    private void moveFolder(MoveFolderCommand command) {
        Folder folderAggregate = repository.load(command.getId());

        if (folderAggregate.getParentFolder().equals(command.getNewParent())) {
            return;
        }

        cz.cvut.fel.integracniportal.model.Folder parentFolder = dao.get(command.getNewParent().getId());

        if (parentFolder != null) {
            boolean fileNameExists = fileNameExistsInFolder(parentFolder, folderAggregate.getName());

            if (fileNameExists) {
                throw new DuplicateNameException();
            }
        } else {
            // TODO duplicate in root folder?
        }

        folderAggregate.moveFolder(command.getNewParent());
    }

    private boolean fileNameExistsInFolder(cz.cvut.fel.integracniportal.model.Folder parentFolder, String name) {
        List<cz.cvut.fel.integracniportal.model.Folder> folders = parentFolder.getFolders();

        if (folders == null) {
            return false;
        }

        for (cz.cvut.fel.integracniportal.model.Folder f : folders) {
            if (f.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void setRepository(Repository<Folder> repository) {
        this.repository = repository;
    }

}
