package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Folder;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * @author Radek Jezdik
 */
public interface FolderDao {

    Folder get(Serializable id);

    void update(Folder folder);

    void delete(Folder folder);

    @Transactional(readOnly = true)
    List<Folder> getTopLevelFolders();

    void createFolder(Folder folder);
}
