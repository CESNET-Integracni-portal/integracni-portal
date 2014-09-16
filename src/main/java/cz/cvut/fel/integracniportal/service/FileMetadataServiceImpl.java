package cz.cvut.fel.integracniportal.service;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata;
import cz.cvut.fel.integracniportal.cesnet.CesnetService;
import cz.cvut.fel.integracniportal.cesnet.FileState;
import cz.cvut.fel.integracniportal.dao.FileMetadataDao;
import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.resource.FileMetadataResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private CesnetService cesnetService;

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
    public String uploadFile(MultipartFile file) throws ServiceAccessException, IOException {
        FileMetadata fileMetadata = new FileMetadata();
        setFileMetadata(fileMetadata, file);
        createFileMetadata(fileMetadata);
        String uuid = fileMetadata.getUuid();

        try {
            cesnetService.uploadFile(file.getInputStream(), uuid);
            return uuid;
        } catch (SftpException e) {
            removeFileMetadata(fileMetadata);
            throw new ServiceAccessException("Cesnet service not available.");
        } catch (IOException e) {
            removeFileMetadata(fileMetadata);
            throw e;
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
            throw new ServiceAccessException("Cesnet service not available.");
        }
    }

    @Override
    public void deleteFile(String uuid) throws ServiceAccessException, FileNotFoundException {
        FileMetadata fileMetadata = getFileMetadataByUuid(uuid);
        try {
            cesnetService.deleteFile(uuid);
            removeFileMetadata(fileMetadata);
        } catch (SftpException e) {
            throw new ServiceAccessException("Cesnet service not available.");
        }
    }

    @Override
    public FileMetadataResource getFileMetadataResource(String fileMetadataUuid) throws ServiceAccessException, FileAccessException, FileNotFoundException {
        FileMetadata fileMetadata = getFileMetadataByUuid(fileMetadataUuid);
        CesnetFileMetadata cesnetFileMetadata = cesnetService.getFileMetadata(fileMetadataUuid);
        FileMetadataResource fileMetadataResource = new FileMetadataResource(fileMetadata, cesnetFileMetadata);
        return fileMetadataResource;
    }

    @Override
    public List<FileMetadataResource> getFileMetadataResources() throws ServiceAccessException, FileAccessException {
        List<CesnetFileMetadata> cesnetFileMetadatas = cesnetService.getFileList();

        return getFileMetadataResourcesFor(cesnetFileMetadatas);
    }

    @Override
    public List<FileMetadataResource> getFileMetadataResources(FileState fileState) throws ServiceAccessException, FileAccessException {
        List<CesnetFileMetadata> cesnetFileMetadatas = cesnetService.getFileListByType(fileState);

        return getFileMetadataResourcesFor(cesnetFileMetadatas);
    }

    private List<FileMetadataResource> getFileMetadataResourcesFor(List<CesnetFileMetadata> cesnetFileMetadatas) {
        List<FileMetadataResource> fileMetadataResources = new ArrayList<FileMetadataResource>();
        for (CesnetFileMetadata cesnetFileMetadata: cesnetFileMetadatas) {
            try {
                FileMetadata fileMetadata = getFileMetadataByUuid(cesnetFileMetadata.getFilename());
                fileMetadataResources.add(new FileMetadataResource(fileMetadata, cesnetFileMetadata));
            } catch (FileNotFoundException e) {
                continue;
            }
        }
        return fileMetadataResources;
    }

    private void setFileMetadata(FileMetadata fileMetadata, MultipartFile file) {
        fileMetadata.setFilename(file.getOriginalFilename());
        fileMetadata.setMimetype(file.getContentType());
    }

}
