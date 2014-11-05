package cz.cvut.fel.integracniportal.service;


import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.representation.FolderRepresentation;

import java.util.List;

/**
 * Service for for {@link cz.cvut.fel.integracniportal.model.Folder}.
 */
public interface FolderService {

    /**
     * Finds a folder in database by its id.
     * @param id    Id of the folder.
     * @return The folder.
     */
    public Folder getFolderById(long id);

    /**
     * Finds a folder in database by its id and returns its representation.
     * @param id    Id of the folder.
     * @return The folder representation.
     */
    public FolderRepresentation getFolderRepresentationById(long id);

    /**
     * Finds all top level folders.
     * @return List of top level folders.
     */
    public List<Folder> getTopLevelFolders();

    /**
     * Finds all top level folders and returns their representations.
     * @return List of top level folder representations.
     */
    public List<FolderRepresentation> getTopLevelFolderRepresentations();

    /**
     * Creates new folder in database.
     * @param folder    Folder which is to be created in the database.
     */
    public Folder createFolder(Folder folder);

    /**
     * Creates new folder in database.
     * @param folderName    Name of the folder to be created.
     * @param parent        Parent folder in which to create the new one.
     */
    public Folder createFolder(String folderName, Folder parent);

    /**
     * Updates existing folder in the database.
     * @param folder    Folder which is to be updated in the database.
     */
    public Folder updateFolder(Folder folder);

    /**
     * Removes a folder from the database.
     * @param folder  Folder to be removed from the database.
     */
    public void removeFolder(Folder folder);

}
