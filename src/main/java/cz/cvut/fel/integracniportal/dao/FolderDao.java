package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.io.Serializable;
import java.util.List;

/**
 * @author Radek Jezdik
 */
public interface FolderDao {

    Folder get(Serializable id);

    void update(Folder folder);

    void delete(Folder folder);

    List<Folder> getTopLevelFolders(UserDetails user);

    List<Folder> getSpaceTopLevelFolders(String spaceId, UserDetails user);

    void createFolder(Folder folder);

}
