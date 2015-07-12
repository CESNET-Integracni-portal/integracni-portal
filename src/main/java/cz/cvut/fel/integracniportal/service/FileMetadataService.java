package cz.cvut.fel.integracniportal.service;


import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * Service for FileMetadata metadata.
 */
public interface FileMetadataService {

    @Transactional(readOnly = true)
    List<FileMetadata> getTopLevelFiles(String spaceId, UserDetails owner);

    /**
     * Finds a file metadata in database by its uuid.
     *
     * @param fileMetadataUuid Uuid of the file.
     * @return FileMetadata metadata.
     */
    public FileMetadata getFileMetadataByUuid(String fileMetadataUuid);

    /**
     * Returns the file metadata.
     *
     * @param fileId the ID of file to return the metadata for
     * @return FileMetadata metadata.
     */
    public FileMetadataRepresentation getFileMetadataRepresentationByUuid(String fileId);

    /**
     * Saves the file metadata into database.
     *
     * @param fileMetadata FileMetadata metadata which are to be saved.
     */
    public void createFileMetadata(FileMetadata fileMetadata);

    /**
     * Updates the file metadata in database.
     *
     * @param fileMetadata FileMetadata metadata which are to be saved.
     */
    public void updateFileMetadata(FileMetadata fileMetadata);

    /**
     * Removes the file metadata from database.
     *
     * @param fileMetadata FileMetadata metadata which are to be removed.
     */
    public void removeFileMetadata(FileMetadata fileMetadata);

    /**
     * Returns list of files that should be deleted according to their deleteOn field.
     *
     * @return List of files to be deleted.
     */
    public List<FileMetadata> getOldFilesForDeletion();

    /**
     * Uploads a file into root space folder and stores its metadata in database.
     *
     * @param space space name to upload the file to
     * @param file  The file to be uploaded.
     * @return Uuid identifier of the created file.
     */
    public FileMetadata uploadFileToRoot(String space, MultipartFile file);

    /**
     * Uploads a file and stores its metadata in database.
     *
     * @param parentFolderId Id of the folder to which the file should be uploaded.
     * @param file           The file to be uploaded.
     * @return Uuid identifier of the created file.
     */
    public FileMetadata uploadFileToFolder(Long parentFolderId, MultipartFile file);

    /**
     * Updates file and its metadata.
     *
     * @param fileuuid Uuid identifier of the file to be updated.
     * @param file     The file to be updated.
     */
    public void updateFile(String fileuuid, MultipartFile file);

    /**
     * Renames file.
     *
     * @param fileId the ID of file to rename
     * @param name   the new file name
     */
    public void renameFile(String fileId, String name);

    /**
     * Moves the file to different folder.
     *
     * @param fileId   the ID of file to move
     * @param parentId the ID of folder to move the file to
     */
    public void moveFile(String fileId, Long parentId);

    /**
     * Deletes a file and removes its metadata from database
     *
     * @param uuid The uuid identifier of the file to be deleted.
     */
    public void deleteFile(String uuid);

    /**
     * Deletes a file and removes its metadata from database
     *
     * @param fileMetadata File to be deleted.
     */
    public void deleteFile(FileMetadata fileMetadata);

    /**
     * Returns the file contents as input stream.
     *
     * @param fileuuid The uuid identifier of the file to be deleted.
     */
    public InputStream getFileAsInputStream(String fileuuid);

    /**
     * Moves the file to online state.
     *
     * @param fileId
     */
    public void moveFileOnline(String fileId);

    /**
     * Moves the file to offline state.
     *
     * @param fileId
     */
    public void moveFileOffline(String fileId);

    /**
     * Marks the file as favorite for the given user.
     *
     * @param fileId      the ID of file to make favorite
     * @param currentUser the user to make the file favorite for
     */
    public void favoriteFile(String fileId, UserDetails currentUser);

    /**
     * Marks the file as not favorite anymore for the given user.
     *
     * @param fileId      the ID of file to unmark as favorite
     * @param currentUser the user to make the file not favorite for
     */
    public void unfavoriteFile(String fileId, UserDetails currentUser);

    /**
     * Shares the file with given users
     *
     * @param fileId      the file to share
     * @param userIds     the IDs of users to share the file with
     * @param currentUser the user who is sharing the file to the other users
     */
    public void shareFile(String fileId, List<Long> userIds, UserDetails currentUser);

}
