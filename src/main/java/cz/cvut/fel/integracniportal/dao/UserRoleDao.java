package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.UserRole;

import java.io.Serializable;
import java.util.List;

/**
 * Data Access Object interface for {@link cz.cvut.fel.integracniportal.model.UserRole}.
 */
public interface UserRoleDao {

    public UserRole get(Serializable id);

    public UserRole getRoleByName(String name);

    public List<UserRole> getAllRoles();

    public void save(UserRole role);

    public void delete(UserRole role);

}
