package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.UserRole;

import java.util.List;

/**
 * Data Access Object interface for {@link cz.cvut.fel.integracniportal.model.UserRole}.
 */
public interface UserRoleDao {

    public UserRole getRoleById(long id);

    public UserRole getRoleByName(String name);

    public List<UserRole> getAllRoles();

    public void saveRole(UserRole role);

    public void deleteRole(UserRole role);

}
