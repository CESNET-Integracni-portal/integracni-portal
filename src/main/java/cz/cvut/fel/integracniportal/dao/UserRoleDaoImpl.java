package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.UserRole;
import org.springframework.stereotype.Repository;

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
    public UserRole getRoleByName(String name) {
        return from(userRole)
                .where(userRole.name.eq(name))
                .uniqueResult(userRole);
    }

    @Override
    public List<UserRole> getAllRoles() {
        return from(userRole)
                .list(userRole);
    }

    @Override
    public boolean userRoleExists(String name) {
        return from(userRole)
                .where(userRole.name.eq(name))
                .exists();
    }

}
