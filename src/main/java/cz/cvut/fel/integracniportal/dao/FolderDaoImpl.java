package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Folder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Hibernate implementation of the {@link cz.cvut.fel.integracniportal.dao.FolderDao} interface.
 */
@Repository
public class FolderDaoImpl extends HibernateDao implements FolderDao {

    @Override
    @Transactional(readOnly = true)
    public Folder getFolderById(long id) {
        return getHibernateTemplate().get(Folder.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> getTopLevelFolders() {
        Criteria c = getCriteria(Folder.class, "folder");
        c.add(Restrictions.isNull("folder.parent"));
        c.addOrder(Order.asc("folder.name"));
        return c.list();
    }

    @Override
    @Transactional
    public void createFolder(Folder folder) {
        Date currentDate = new Date();
        folder.setCreatedOn(currentDate);
        folder.setChangedOn(currentDate);
        getHibernateTemplate().save(folder);
    }

    @Override
    @Transactional
    public void updateFolder(Folder folder) {
        folder.setChangedOn(new Date());
        getHibernateTemplate().update(folder);
    }

    @Override
    @Transactional
    public void removeFolder(Folder folder) {
        getHibernateTemplate().delete(folder);
    }
}
