package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.api.*;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The adapter for accessing file repository using {@link cz.cvut.fel.integracniportal.model.FileMetadata file metadata entity} and
 * {@link cz.cvut.fel.integracniportal.model.Folder folder entity} and {@link cz.cvut.fel.integracniportal.model.UserDetails entity}.
 *
 * @author Radek Jezdik
 */
public class FileApiAdapter {

    private FileRepository fileRepository;

    public FileApiAdapter(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void putFile(FileMetadata fileMetadata, InputStream inputStream) {
        fileRepository.putFile(mapFile(fileMetadata), inputStream);
    }

    public void createFolder(Folder folder) {
        fileRepository.createFolder(mapFolder(folder));
    }


    public void moveFileToBin(FileMetadata fileMetadata) {
        if ((fileRepository instanceof BinFileRepository) == false) {
            throw new IllegalStateException("Repository doesn't support bin");
        }
        FileDefinition file = mapFile(fileMetadata);
        ((BinFileRepository) fileRepository).moveFileToBin(file);
    }

    public void moveFolderToBin(Folder folder) {
        if ((fileRepository instanceof BinFileRepository) == false) {
            throw new IllegalStateException("Repository doesn't support bin");
        }
        FolderDefinition folderDef = mapFolder(folder);
        ((BinFileRepository) fileRepository).moveFolderToBin(folderDef);
    }

    public FileDefinition getFileDefinition(FileMetadata fileMetadata) {
        return fileRepository.getFileMetadata(mapFile(fileMetadata));
    }

    public InputStream getFile(FileMetadata fileMetadata) {
        return fileRepository.getFile(mapFile(fileMetadata));
    }

    public void renameFolder(Folder folder, String newName) {
        fileRepository.renameFolder(newName, mapFolder(folder));
    }

    public void moveFolder(Folder folder, Folder parent) {
        fileRepository.moveFolder(mapFolder(folder), mapFolder(parent));
    }

    public void moveFolderOnline(Folder folder) {
        if (fileRepository instanceof OfflinableFileRepository) {
            // TODO
//            ((OfflinableFileRepository) fileRepository).moveOnline();
        }
    }

    public void moveFolderOffline(Folder folder) {
        if (fileRepository instanceof OfflinableFileRepository) {
            // TODO
//            ((OfflinableFileRepository) fileRepository).moveOffline();
        }
    }

    public void renameFile(FileMetadata fileMetadata, String newName) {
        fileRepository.renameFile(newName, mapFile(fileMetadata));
    }

    public void moveFile(FileMetadata fileMetadata, Folder parent) {
        fileRepository.moveFile(mapFile(fileMetadata), mapFolder(parent));
    }

    public void moveFileOnline(FileMetadata fileMetadata) {
        if (fileRepository instanceof OfflinableFileRepository) {
            // TODO
//            ((OfflinableFileRepository) fileRepository).moveOnline();
        }
    }

    public void moveFileOffline(FileMetadata fileMetadata) {
        if (fileRepository instanceof OfflinableFileRepository) {
            // TODO
//            ((OfflinableFileRepository) fileRepository).moveOffline();
        }
    }

    private FolderDefinition mapFolder(Folder folder) {
        if (folder == null) {
            return FolderDefinition.ROOT_FOLDER;
        }

        FolderDefinition folderDefinition = new FolderDefinition();

        folderDefinition.setId(folder.getId());
        folderDefinition.setName(folder.getName());
        folderDefinition.setOwner(mapUser(folder.getOwner()));
        folderDefinition.setPath(createFolderPath(folder));

        return folderDefinition;
    }

    private String createFolderPath(Folder folder) {
        List<String> path = new ArrayList<String>();

        while (folder != null) {
            path.add(0, folder.getName());
            folder = folder.getParent();
        }

        return String.join("/", path);
    }

    private User mapUser(UserDetails owner) {
        User user = new User();

        user.setId(owner.getId());
        user.setUsername(owner.getUsername());

        return user;
    }

    private FileDefinition mapFile(FileMetadata fileMetadata) {
        FileDefinition fileDefinition = new FileDefinition();

        fileDefinition.setId(fileMetadata.getId());
        fileDefinition.setName(fileMetadata.getFilename());
        fileDefinition.setSize(fileMetadata.getFilesize());
        fileDefinition.setMimeType(fileMetadata.getMimetype());
        fileDefinition.setDateCreated(fileMetadata.getCreatedOn());
        fileDefinition.setFolder(mapFolder(fileMetadata.getParent()));

        return fileDefinition;
    }
}
