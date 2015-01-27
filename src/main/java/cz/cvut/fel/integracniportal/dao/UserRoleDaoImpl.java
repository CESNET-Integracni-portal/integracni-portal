package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.UserRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cz.cvut.fel.integracniportal.model.QUserRole.userRole;

/**
 * Hibernate implementation of the {@link cz.cvut.fel.integracniportal.dao.UserRoleDao} interface.
 */
@Repository
public class UserRoleDaoImpl extends GenericHibernateDao<UserRole> implements UserRoleDao {

    public UserRoleDaoImpl() {
        super(UserRole.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRole getRoleByName(String name) {
        return from(userRole)
                .where(userRole.name.eq(name))
                .uniqueResult(userRole);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRole> getAllRoles() {
        return from(userRole)
                .list(userRole);
    }

}
