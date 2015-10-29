package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.UserRole;
import cz.cvut.fel.integracniportal.representation.UserRoleRepresentation;

import java.util.List;

/**
 * Service for {@link cz.cvut.fel.integracniportal.model.UserRole}.
 */
public interface UserRoleService {

    /**
     * Finds a user role in database by its id.
     *
     * @param roleId Id of the role.
     * @return The user role.
     */
    public UserRole getRoleById(String roleId);

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
     */
    public UserRole createRole(UserRoleRepresentation role);

    /**
     * The user role representation to update.
     * @param roleId
     * @param userRoleRepresentation
     */
    public void updateRole(String roleId, UserRoleRepresentation userRoleRepresentation);

    /**
     * Removes the user role from database.
     *
     * @param roleId UserRole id to be removed.
     */
    public void deleteRole(String roleId);
}
