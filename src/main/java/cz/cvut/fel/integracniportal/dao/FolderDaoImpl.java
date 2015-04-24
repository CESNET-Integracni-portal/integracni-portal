package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.Label;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static cz.cvut.fel.integracniportal.model.QFolder.folder;

/**
 * Hibernate implementation of the {@link FolderDao} interface.
 */
@Repository
public class FolderDaoImpl extends GenericHibernateDao<Folder> implements FolderDao {

    public FolderDaoImpl() {
        super(Folder.class);
    }

    @Override
    public Folder getForUser(Long id, UserDetails currentUser) {
//        enableFilter(Label.USERS_LABELS_FILTER)
//                .setParameter("userId", currentUser.getId());

        return from(folder)
                .where(folder.folderId.eq(id))
                .singleResult(folder);

    }

    @Override
    public List<Folder> getTopLevelFolders(UserDetails user) {
        enableFilter(Label.USERS_LABELS_FILTER)
                .setParameter("userId", user.getId());

        return from(folder)
                .where(folder.parent.isNull())
                .where(folder.owner.eq(user))
                .where(folder.deleted.isFalse())
                .orderBy(folder.name.asc())
                .list(folder);
    }

    @Override
    public List<Folder> getSpaceTopLevelFolders(String spaceId, UserDetails user) {
        return from(folder)
                .where(folder.parent.isNull())
                .where(folder.space.eq(spaceId))
                .where(folder.owner.eq(user))
                .where(folder.deleted.isFalse())
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
