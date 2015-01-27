package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Folder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static cz.cvut.fel.integracniportal.model.QFolder.folder;

/**
 * Hibernate implementation of the {@link FolderDao} interface.
 */
@Repository
@Transactional
public class FolderDaoImpl extends GenericHibernateDao<Folder> implements FolderDao {

    public FolderDaoImpl() {
        super(Folder.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> getTopLevelFolders() {
        return from(folder)
                .where(folder.parent.isNull())
                .orderBy(folder.name.asc())
                .list(folder);
    }

    @Override
    public void createFolder(Folder folder) {
        Date currentDate = new Date();
        folder.setCreatedOn(currentDate);
        folder.setChangedOn(currentDate);
        save(folder);
    }

}
