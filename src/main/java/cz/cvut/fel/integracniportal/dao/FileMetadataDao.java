package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.util.List;

/**
 * Data Access Object interface for FileMetadata metadata.
 */
public interface FileMetadataDao {

    /**
     * Finds a file metadata in database by its uuid.
     *
     * @param fileMetadataUuid Uuid of the file.
     * @return FileMetadata metadata.
     */
    public FileMetadata getByUUID(String fileMetadataUuid);

    /**
     * Finds all files in database.
     *
     * @return List of FileMetadata metadata.
     */
    public List<FileMetadata> getAllFileMetadatas();

    /**
     * Creates new file metadata and saves them into database.
     *
     * @param fileMetadata FileMetadata metadata which are to be saved.
     */
    public void createFileMetadata(FileMetadata fileMetadata);

    /**
     * Updates existing file metadata and saves them into database.
     *
     * @param fileMetadata FileMetadata metadata which are to be saved.
     */
    public void update(FileMetadata fileMetadata);

    /**
     * Removes the file metadata from database.
     *
     * @param fileMetadata FileMetadata metadata which are to be removed.
     */
    public void delete(FileMetadata fileMetadata);

    /**
     * Returns list of files that should be deleted according to their deleteOn field.
     *
     * @return List of files to be deleted.
     */
    public List<FileMetadata> getFilesForDeletion();

    public List<FileMetadata> getAllTopLevelFiles(String spaceId, UserDetails owner);

    public List<FileMetadata> getFilesByLabels(String spaceId, List<Long> labelIds, UserDetails user);
}
