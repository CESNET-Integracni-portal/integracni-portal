package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.UserRole;

import java.util.List;

/**
 * Service for {@link cz.cvut.fel.integracniportal.model.UserRole}.
 */
public interface UserRoleService {

    /**
     * Finds a user role in database by its id.
     * @param id    Id of the role.
     * @return UserRole.
     */
    public UserRole getRoleById(long id);

    /**
     * Finds a user role in database by its name.
     * @param name  Name of the role.
     * @return UserRole.
     */
    public UserRole getRoleByName(String name);

    /**
     * Finds all user roles in the database.
     * @return List of user roles.
     */
    public List<UserRole> getAllRoles();

    /**
     * Saves the user role into database.
     * @param role    UserRole to be saved.
     */
    public void saveRole(UserRole role);

    /**
     * Removes the user role from database.
     * @param role  UserRole to be removed.
     */
    public void deleteRole(UserRole role);
}
