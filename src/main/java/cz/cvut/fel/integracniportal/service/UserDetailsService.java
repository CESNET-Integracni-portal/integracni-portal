package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.exceptions.UserRoleNotFoundException;
import cz.cvut.fel.integracniportal.model.Permission;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;

import java.util.List;

/**
 * Service for UserDetails login credentials.
 */
public interface UserDetailsService {

    /**
     * Finds a user in database by his id.
     * @param userId Id of the user.
     * @return UserDetails login credentials.
     * @throws NotFoundException the not found exception
     */
    public UserDetails getUserById(long userId) throws NotFoundException;

    /**
     * Finds a user in database by his username.
     * @param username Username of the user.
     * @return UserDetails login credentials.
     */
    public UserDetails getUserByUsername(String username);

    /**
     * Gets the currently logged in user.
     *
     * @return UserDetails for the current user.
     */
    public UserDetails getCurrentUser();

    /**
     * Finds all users in the database.
     * @return List of users.
     */
    public List<UserDetails> getAllUsers();

    /**
     * Finds all users in an organizational unit.
     *
     * @param organizationalUnitId Id of the organizational unit.
     * @return List of users in the organizational unit
     */
    public List<UserDetails> getAllUsersInOrganizationalUnit(Long organizationalUnitId);

    /**
     * Finds admins for an organizational unit.
     *
     * @param organizationalUnitId Id of the organizational unit.
     * @return List of admins for the organizational unit
     */
    public List<UserDetails> getAdminsForOrganizationalUnit(Long organizationalUnitId);

    /**
     * Creates a user from supplied {@link cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation}.
     *
     * @param userDetailsResource the user details
     * @return the user details
     * @throws UserRoleNotFoundException the user role not found exception
     * @throws AlreadyExistsException the already exists exception
     */
    public UserDetails createUser(UserDetailsRepresentation userDetailsResource) throws UserRoleNotFoundException, AlreadyExistsException;

    /**
     * Updates a user from supplied {@link cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation}.
     *
     * @param userId id of the user to be updated
     * @param userDetailsResource the user details
     * @return the user details
     * @throws UserRoleNotFoundException the user role not found exception
     * @throws NotFoundException the not found exception
     */
    public UserDetails updateUser(Long userId, UserDetailsRepresentation userDetailsResource) throws UserRoleNotFoundException, NotFoundException;

    /**
     * Removes a permission from user.
     *
     * @param userDetails the user
     * @param permission the permission to be removed from user
     */
    public void removePermissionFromUser(UserDetails userDetails, Permission permission);

    /**
     * Saves the user into database.
     * @param user UserDetails login credentials which are to be saved.
     */
    public void saveUser(UserDetails user);

    /**
     * Removes the user from database.
     * @param user UserDetails login credentials which are to be removed.
     */
    public void removeUser(UserDetails user);
}
