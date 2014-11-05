package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Folder;

import java.util.List;

/**
 * Data Access Object interface for {@link cz.cvut.fel.integracniportal.model.Folder} entity.
 */
public interface FolderDao {

    /**
     * Finds a folder in database by its id.
     * @param id    Id of the folder.
     * @return The folder.
     */
    public Folder getFolderById(long id);

    /**
     * Finds all top level folders.
     * @return List of top level folders.
     */
    public List<Folder> getTopLevelFolders();

    /**
     * Creates new folder in database.
     * @param folder    Folder which is to be created in the database.
     */
    public void createFolder(Folder folder);

    /**
     * Updates existing folder in the database.
     * @param folder    Folder which is to be updated in the database.
     */
    public void updateFolder(Folder folder);

    /**
     * Removes a folder from the database.
     * @param folder  Folder to be removed from the database.
     */
    public void removeFolder(Folder folder);

}
