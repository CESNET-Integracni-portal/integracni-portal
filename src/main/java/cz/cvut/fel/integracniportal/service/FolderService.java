package cz.cvut.fel.integracniportal.service;


import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.FolderRepresentation;
import cz.cvut.fel.integracniportal.representation.TopLevelFolderRepresentation;

import java.util.List;

/**
 * Service for for {@link cz.cvut.fel.integracniportal.model.Folder}.
 */
public interface FolderService {

    /**
     * Finds a folder in database by its id.
     *
     * @param id Id of the folder.
     * @return The folder.
     */
    public Folder getFolderById(long id);

    /**
     * Finds a folder in database by its id and returns its representation.
     *
     * @param id Id of the folder.
     * @return The folder representation.
     */
    public FolderRepresentation getFolderRepresentationById(long id);

    /**
     * Finds all top level folders.
     *
     * @return List of top level folders.
     */
    public List<Folder> getTopLevelFolders(String spaceId, UserDetails owner);

    /**
     * Finds all top level folders and returns their representations.
     *
     * @return List of top level folder representations.
     */
    public TopLevelFolderRepresentation getTopLevelFolder(String spaceId, UserDetails owner);

    /**
     * Creates new folder in database.
     *
     * @param folder Folder which is to be created in the database.
     */
    public Folder createFolder(Folder folder, UserDetails owner);

    /**
     * Creates new top level folder in database.
     *
     * @param folderName Name of the folder to be created.
     */
    public Folder createTopLevelFolder(String folderName, String spaceId, UserDetails owner);

    /**
     * Creates new folder in database.
     *
     * @param folderName Name of the folder to be created.
     * @param parentId   Id of the parent folder in which to create the new one.
     */
    public Folder createSubFolder(String folderName, Long parentId, UserDetails owner);

    /**
     * Creates new folder in database.
     *
     * @param folderName Name of the folder to be created.
     * @param parent     Parent folder in which to create the new one.
     */
    public Folder createSubFolder(String folderName, Folder parent, UserDetails owner);

    /**
     * Updates existing folder in the database.
     *
     * @param folderId Id of the folder which is to be updated in the database.
     * @param newName  the new name of the folder
     */
    public Folder renameFolder(Long folderId, String newName);

    /**
     * Removes a folder from the database.
     *
     * @param folderId Id of the folder to be removed from the database.
     */
    public void removeFolder(Long folderId);

    /**
     * Updates the given folder entity.
     *
     * @param folder the folder entity to update
     */
    public void updateFolder(Folder folder);

    /**
     * Moves the folder under different parent folder.
     *
     * @param folderId the id of the folder to move
     * @param parentId the id of the new parent
     */
    public void moveFolder(Long folderId, Long parentId);

    /**
     * Moves the folder to online state.
     *
     * @param folderId
     */
    public void moveFolderOnline(Long folderId);

    /**
     * Moves the folder to offline state.
     *
     * @param folderId
     */
    public void moveFolderOffline(Long folderId);

    /**
     * Adds a label to folder.
     *
     * @param folderId    the ID of folder to add the label to
     * @param labelId     the ID of label to add
     * @param currentUser the user to add the label for
     */
    public void addLabel(Long folderId, Long labelId, UserDetails currentUser);

    /**
     * Removes a label from folder.
     *
     * @param folderId    the ID of folder to remove the label from.
     * @param labelId     the ID of label to remove
     * @param currentUser the user to remove the label for
     */
    public void removeLabel(Long folderId, Long labelId, UserDetails currentUser);

    /**
     * Marks the folder as favorite for the given user.
     *
     * @param folderId    the ID of folder to make favorite
     * @param currentUser the user to make the folder favorite for
     */
    public void favoriteFolder(Long folderId, UserDetails currentUser);

    /**
     * Marks the folder as not favorite anymore for the given user.
     *
     * @param folderId    the ID of folder to unmark as favorite
     * @param currentUser the user to make the folder not favorite for
     */
    public void unfavoriteFolder(Long folderId, UserDetails currentUser);

    /**
     * Shares the folder with given users
     *
     * @param folderId    the folder to share
     * @param userIds     the IDs of users to share the folder with
     * @param currentUser the user who is sharing the folder to the other users
     */
    public void shareFolder(Long folderId, List<Long> userIds, UserDetails currentUser);

}