package cz.cvut.fel.integracniportal.service;


import cz.cvut.fel.integracniportal.cesnet.FileState;
import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.resource.FileMetadataResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Service for FileMetadata metadata.
 */
public interface FileMetadataService {

    /**
     * Finds a resource file metadata in database by its uuid.
     * @param fileMetadataUuid    Uuid of the file.
     * @return FileMetadata metadata.
     */
    public FileMetadata getFileMetadataByUuid(String fileMetadataUuid) throws FileNotFoundException;

    /**
     * Saves the file metadata into database.
     * @param fileMetadata    FileMetadata metadata which are to be saved.
     */
    public void createFileMetadata(FileMetadata fileMetadata);

    /**
     * Updates the file metadata in database.
     * @param fileMetadata    FileMetadata metadata which are to be saved.
     */
    public void updateFileMetadata(FileMetadata fileMetadata);

    /**
     * Removes the file metadata from database.
     * @param fileMetadata  FileMetadata metadata which are to be removed.
     */
    public void removeFileMetadata(FileMetadata fileMetadata);

    /**
     * Uploads a file and stores its metadata in database.
     * @param file    The file to be uploaded.
     * @return Uuid identifier of the created file.
     */
    public String uploadFile(MultipartFile file) throws IOException, ServiceAccessException;

    /**
     * Updates file and its metadata.
     * @param fileuuid    Uuid identifier of the file to be updated.
     * @param file    The file to be updated.
     */
    public void updateFile(String fileuuid, MultipartFile file) throws IOException, ServiceAccessException;

    /**
     * Deletes a file and removes its metadata from database
     * @param uuid    The uuid identifier of the file to be deleted.
     */
    public void deleteFile(String uuid) throws ServiceAccessException, FileNotFoundException;

    /**
     * Returns full metadata information about a file.
     * @param fileMetadataUuid    Uuid of the file.
     * @return Full file metadata.
     */
    public FileMetadataResource getFileMetadataResource(String fileMetadataUuid) throws ServiceAccessException, FileAccessException, FileNotFoundException;

    /**
     * Returns full metadata information about all files.
     * @return List of full file metadata.
     */
    public List<FileMetadataResource> getFileMetadataResources() throws ServiceAccessException, FileAccessException;

    /**
     * Returns full metadata information about all files in certain state.
     * @param fileState    State by which the files will be filtered.
     * @return List of full file metadata.
     */
    public List<FileMetadataResource> getFileMetadataResources(FileState fileState) throws ServiceAccessException, FileAccessException;
}
