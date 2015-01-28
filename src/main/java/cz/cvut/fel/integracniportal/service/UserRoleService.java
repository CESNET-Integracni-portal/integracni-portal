package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.exceptions.PermissionNotAssignableToRoleException;
import cz.cvut.fel.integracniportal.model.UserRole;

import java.util.List;

/**
 * Service for {@link cz.cvut.fel.integracniportal.model.UserRole}.
 */
public interface UserRoleService {

    /**
     * Finds a user role in database by its id.
     *
     * @param id Id of the role.
     * @return The user role.
     * @throws NotFoundException role not found
     */
    public UserRole getRoleById(long id) throws NotFoundException;

    /**
     * Finds a user role in database by its name.
     *
     * @param name Name of the role.
     * @return The user role.
     */
    public UserRole getRoleByName(String name);

    /**
     * Finds all user roles in the database.
     *
     * @return List of user roles.
     */
    public List<UserRole> getAllRoles();

    /**
     * Create a new user role.
     *
     * @param role The user role to save to database
     * @throws AlreadyExistsException role already exists
     */
    public void createRole(UserRole role) throws AlreadyExistsException, PermissionNotAssignableToRoleException;

    /**
     * Saves the user role into database.
     *
     * @param role UserRole to be saved.
     */
    public void saveRole(UserRole role) throws PermissionNotAssignableToRoleException;

    /**
     * Removes the user role from database.
     *
     * @param role UserRole to be removed.
     */
    public void deleteRole(UserRole role);
}
