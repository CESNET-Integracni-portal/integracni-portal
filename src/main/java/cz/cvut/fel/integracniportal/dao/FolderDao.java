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

    Folder getForUser(Long id, UserDetails currentUser);

    void update(Folder folder);

    void delete(Folder folder);

    List<Folder> getSpaceTopLevelFolders(String spaceId, UserDetails user);

    List<Folder> getFoldersByLabels(String spaceId, List<Long> labelIds, UserDetails owner);

    void createFolder(Folder folder);
}
