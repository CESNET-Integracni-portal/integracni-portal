package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.QFolder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static cz.cvut.fel.integracniportal.model.QFolder.folder;
import static cz.cvut.fel.integracniportal.model.QLabel.label;

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
        return from(folder)
                .where(folder.folderId.eq(id))
                .singleResult(folder);

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
    public List<Folder> getFoldersByLabels(String spaceId, List<Long> labelIds, UserDetails user) {
        QFolder folder2 = new QFolder("folder2");
        return from(folder)
                .where(folder.space.eq(spaceId))
                .where(folder.owner.eq(user))
                .where(folder.deleted.isFalse())
                .where(folder.notIn(subQuery()
                        .from(folder2)
                        .leftJoin(folder2.labels, label)
                        .where(label.labelId.notIn(labelIds).or(folder2.labels.isEmpty()))
                        .list(folder2)))
                .orderBy(folder.name.asc())
                .distinct()
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
