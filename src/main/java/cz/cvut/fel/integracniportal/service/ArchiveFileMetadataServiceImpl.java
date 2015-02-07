package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata;
import cz.cvut.fel.integracniportal.cesnet.CesnetService;
import cz.cvut.fel.integracniportal.cesnet.FileState;
import cz.cvut.fel.integracniportal.dao.FileMetadataDao;
import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.exceptions.FileIOException;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.CesnetFileMetadataRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link ArchiveFileMetadataService}.
 */
@Service
public class ArchiveFileMetadataServiceImpl implements ArchiveFileMetadataService {

    @Autowired
    private FileMetadataDao fileMetadataDao;

    @Autowired
    private ArchiveFolderService archiveFolderService;

    @Autowired
    private CesnetService cesnetService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public FileMetadata getFileMetadataByUuid(String fileMetadataUuid) {
        return fileMetadataDao.getByUUID(fileMetadataUuid);
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
    public List<FileMetadata> getOldFilesForDeletion() {
        return fileMetadataDao.getFilesForDeletion();
    }

    @Override
    @Transactional
    public FileMetadata uploadFileToFolder(Long parentFolderId, MultipartFile file) {
        Folder parent = archiveFolderService.getFolderById(parentFolderId);
        return uploadFile(parent, file);
    }

    private FileMetadata uploadFile(Folder folder, MultipartFile file) {
        FileMetadata fileMetadata = new FileMetadata();
        setFileMetadata(fileMetadata, file);
        fileMetadata.setParent(folder);
        UserDetails currentUser = userDetailsService.getCurrentUser();
        fileMetadata.setOwner(currentUser);
        createFileMetadata(fileMetadata);
        String uuid = fileMetadata.getUuid();

        try {
            cesnetService.uploadFile(file.getInputStream(), uuid);
            archiveFolderService.updateFolder(folder);
            return fileMetadata;
        } catch (IOException e) {
            throw new FileIOException("Could not read uploaded file", e, "cesnet.service.unavailable");
        }
    }

    @Override
    public void updateFile(String fileuuid, MultipartFile file) {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileuuid);
        try {
            cesnetService.uploadFile(file.getInputStream(), fileuuid);
            setFileMetadata(fileMetadata, file);
            updateFileMetadata(fileMetadata);
        } catch (IOException e) {
            throw new FileIOException("Could not read uploaded file", e, "cesnet.service.unavailable");
        }
    }

    @Override
    @Transactional(rollbackFor = {ServiceAccessException.class, FileNotFoundException.class})
    public void deleteFile(String uuid) {
        FileMetadata fileMetadata = getFileMetadataByUuid(uuid);
        deleteFile(fileMetadata);
    }

    @Override
    @Transactional(rollbackFor = ServiceAccessException.class)
    public void deleteFile(FileMetadata fileMetadata) {
        removeFileMetadata(fileMetadata);
        cesnetService.deleteFile(fileMetadata.getUuid());
    }

    @Override
    public CesnetFileMetadataRepresentation getFileMetadataResource(String fileMetadataUuid) {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileMetadataUuid);
        CesnetFileMetadata cesnetFileMetadata = cesnetService.getFileMetadata(fileMetadataUuid);
        return new CesnetFileMetadataRepresentation(fileMetadata, cesnetFileMetadata);
    }

    @Override
    public List<CesnetFileMetadataRepresentation> getFileMetadataResources() {
        return getFileMetadataResourcesFor(cesnetService.getFileList());
    }

    @Override
    public List<CesnetFileMetadataRepresentation> getFileMetadataResources(FileState fileState) {
        return getFileMetadataResourcesFor(cesnetService.getFileListByType(fileState));
    }

    private List<CesnetFileMetadataRepresentation> getFileMetadataResourcesFor(List<CesnetFileMetadata> cesnetFileMetadatas) {
        List<CesnetFileMetadataRepresentation> fileMetadataResources = new ArrayList<CesnetFileMetadataRepresentation>();
        for (CesnetFileMetadata cesnetFileMetadata : cesnetFileMetadatas) {
            try {
                FileMetadata fileMetadata = getFileMetadataByUuid(cesnetFileMetadata.getFilename());
                fileMetadataResources.add(new CesnetFileMetadataRepresentation(fileMetadata, cesnetFileMetadata));
            } catch (FileAccessException e) {
                continue;
            }
        }
        return fileMetadataResources;
    }

    private void setFileMetadata(FileMetadata fileMetadata, MultipartFile file) {
        fileMetadata.setFilename(file.getOriginalFilename());
        fileMetadata.setMimetype(file.getContentType());
        fileMetadata.setFilesize(file.getSize());
    }

}
