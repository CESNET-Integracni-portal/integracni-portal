package cz.cvut.fel.integracniportal.service;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata;
import cz.cvut.fel.integracniportal.cesnet.CesnetService;
import cz.cvut.fel.integracniportal.cesnet.FileState;
import cz.cvut.fel.integracniportal.dao.FileMetadataDao;
import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
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
 * Implementation of the {@link FileMetadataService}.
 */
@Service
public class FileMetadataServiceImpl implements FileMetadataService {

    @Autowired
    private FileMetadataDao fileMetadataDao;

    @Autowired
    private FolderService folderService;

    @Autowired
    private CesnetService cesnetService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public FileMetadata getFileMetadataByUuid(String fileMetadataUuid) throws FileNotFoundException {
        return fileMetadataDao.getFileMetadataByUuid(fileMetadataUuid);
    }

    @Override
    public void createFileMetadata(FileMetadata fileMetadata) {
        fileMetadataDao.createFileMetadata(fileMetadata);
    }

    @Override
    public void updateFileMetadata(FileMetadata fileMetadata) {
        fileMetadataDao.updateFileMetadata(fileMetadata);
    }

    @Override
    public void removeFileMetadata(FileMetadata fileMetadata) {
        fileMetadataDao.removeFileMetadata(fileMetadata);
    }

    @Override
    public List<FileMetadata> getOldFilesForDeletion() {
        return fileMetadataDao.getFilesForDeletion();
    }

    @Override
    @Transactional(rollbackFor = {ServiceAccessException.class, IOException.class, NotFoundException.class})
    public FileMetadata uploadFile(Long folderId, MultipartFile file) throws IOException, ServiceAccessException, NotFoundException {
        Folder parent = folderService.getFolderById(folderId);
        return uploadFile(parent, file);
    }

    @Override
    @Transactional(rollbackFor = {ServiceAccessException.class, IOException.class})
    public FileMetadata uploadFile(Folder folder, MultipartFile file) throws ServiceAccessException, IOException {
        FileMetadata fileMetadata = new FileMetadata();
        setFileMetadata(fileMetadata, file);
        fileMetadata.setParent(folder);
        UserDetails currentUser = userDetailsService.getCurrentUser();
        fileMetadata.setOwner(currentUser);
        createFileMetadata(fileMetadata);
        String uuid = fileMetadata.getUuid();

        try {
            cesnetService.uploadFile(file.getInputStream(), uuid);
            folderService.updateFolder(folder);
            return fileMetadata;
        } catch (SftpException e) {
            throw new ServiceAccessException("cesnet.service.unavailable");
        }
    }

    @Override
    public void updateFile(String fileuuid, MultipartFile file) throws ServiceAccessException, IOException {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileuuid);
        try {
            cesnetService.uploadFile(file.getInputStream(), fileuuid);
            setFileMetadata(fileMetadata, file);
            updateFileMetadata(fileMetadata);
        } catch (SftpException e) {
            throw new ServiceAccessException("cesnet.service.unavailable");
        }
    }

    @Override
    @Transactional(rollbackFor = {ServiceAccessException.class, FileNotFoundException.class})
    public void deleteFile(String uuid) throws ServiceAccessException, FileNotFoundException {
        FileMetadata fileMetadata = getFileMetadataByUuid(uuid);
        deleteFile(fileMetadata);
    }

    @Override
    @Transactional(rollbackFor = ServiceAccessException.class)
    public void deleteFile(FileMetadata fileMetadata) throws ServiceAccessException {
        try {
            removeFileMetadata(fileMetadata);
            cesnetService.deleteFile(fileMetadata.getUuid());
        } catch (SftpException e) {
            throw new ServiceAccessException("cesnet.service.unavailable");
        }
    }

    @Override
    public CesnetFileMetadataRepresentation getFileMetadataResource(String fileMetadataUuid) throws ServiceAccessException, FileAccessException, FileNotFoundException {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileMetadataUuid);
        CesnetFileMetadata cesnetFileMetadata = cesnetService.getFileMetadata(fileMetadataUuid);
        CesnetFileMetadataRepresentation fileMetadataResource = new CesnetFileMetadataRepresentation(fileMetadata, cesnetFileMetadata);
        return fileMetadataResource;
    }

    @Override
    public List<CesnetFileMetadataRepresentation> getFileMetadataResources() throws ServiceAccessException, FileAccessException {
        List<CesnetFileMetadata> cesnetFileMetadatas = cesnetService.getFileList();

        return getFileMetadataResourcesFor(cesnetFileMetadatas);
    }

    @Override
    public List<CesnetFileMetadataRepresentation> getFileMetadataResources(FileState fileState) throws ServiceAccessException, FileAccessException {
        List<CesnetFileMetadata> cesnetFileMetadatas = cesnetService.getFileListByType(fileState);

        return getFileMetadataResourcesFor(cesnetFileMetadatas);
    }

    private List<CesnetFileMetadataRepresentation> getFileMetadataResourcesFor(List<CesnetFileMetadata> cesnetFileMetadatas) {
        List<CesnetFileMetadataRepresentation> fileMetadataResources = new ArrayList<CesnetFileMetadataRepresentation>();
        for (CesnetFileMetadata cesnetFileMetadata: cesnetFileMetadatas) {
            try {
                FileMetadata fileMetadata = getFileMetadataByUuid(cesnetFileMetadata.getFilename());
                fileMetadataResources.add(new CesnetFileMetadataRepresentation(fileMetadata, cesnetFileMetadata));
            } catch (FileNotFoundException e) {
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
