package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.UserRole;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Hibernate implementation of the {@link cz.cvut.fel.integracniportal.dao.UserRoleDao} interface.
 */
@Repository
public class UserRoleDaoImpl extends HibernateDao implements UserRoleDao {

    @Override
    @Transactional(readOnly = true)
    public UserRole getRoleById(long id) {
        return getHibernateTemplate().get(UserRole.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRole getRoleByName(String name) {
        Criteria criteria = getCriteria(UserRole.class, "userrole");
        criteria.add(Restrictions.eq("userrole.name", name));
        return (UserRole) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRole> getAllRoles() {
        Criteria criteria = getCriteria(UserRole.class, "userrole");
        return criteria.list();
    }

    @Override
    @Transactional
    public void saveRole(UserRole role) {
        getHibernateTemplate().saveOrUpdate(role);
    }

    @Override
    @Transactional
    public void deleteRole(UserRole role) {
        getHibernateTemplate().delete(role);
    }
}
