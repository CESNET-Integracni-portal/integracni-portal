package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.FileMetadata;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Data Access Object interface for FileMetadata metadata.
 */
public interface FileMetadataDao {

    /**
     * Finds a resource file metadata in database by its uuid.
     * @param fileMetadataUuid    Uuid of the file.
     * @return FileMetadata metadata.
     */
    public FileMetadata getFileMetadataByUuid(String fileMetadataUuid) throws FileNotFoundException;

    /**
     * Finds all resource files in database.
     * @return List of FileMetadata metadata.
     */
    public List<FileMetadata> getAllFileMetadatas();

    /**
     * Creates new file metadata and saves them into database.
     * @param fileMetadata    FileMetadata metadata which are to be saved.
     */
    public void createFileMetadata(FileMetadata fileMetadata);

    /**
     * Updates existing file metadata and saves them into database.
     * @param fileMetadata    FileMetadata metadata which are to be saved.
     */
    public void updateFileMetadata(FileMetadata fileMetadata);

    /**
     * Removes the file metadata from database.
     * @param fileMetadata  FileMetadata metadata which are to be removed.
     */
    public void removeFileMetadata(FileMetadata fileMetadata);

}
