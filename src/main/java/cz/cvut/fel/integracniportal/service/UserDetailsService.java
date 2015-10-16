package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;

import java.util.List;

/**
 * Service for UserDetails login credentials.
 */
public interface UserDetailsService {

    /**
     * Finds a user in database by his id.
     *
     * @param userId Id of the user.
     * @return UserDetails login credentials.
     */
    public UserDetails getUserById(String userId);

    /**
     * Finds a user in database by his username.
     *
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
     *
     * @return List of users.
     */
    public List<UserDetails> getAllUsers();

    /**
     * Finds all users in an organizational unit.
     *
     * @param organizationalUnitId Id of the organizational unit.
     * @return List of users in the organizational unit
     */
    public List<UserDetails> getAllUsersInOrganizationalUnit(String organizationalUnitId);

    /**
     * Creates a user from supplied {@link cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation}.
     *
     * @param userDetailsResource the user details
     * @return the user details
     */
    public UserDetails createUser(UserDetailsRepresentation userDetailsResource);

    /**
     * Saves the user into database.
     *
     * @param user UserDetails login credentials which are to be saved.
     */
    public void saveUser(UserDetails user);

    /**
     * Removes the user from database.
     *
     * @param user UserDetails login credentials which are to be removed.
     */
    public void removeUser(UserDetails user);

    /**
     * Changes the user's password
     * @param token
     * @param userId id of the user for which to change the password
     * @param newPassword the new password for the user
     * @param oldPassword the old password for check
     */
    public void changePassword(String userId, String newPassword, String oldPassword);

    /**
     * Updates the user's roles to the given list of roles
     * @param userId id of the user for which to update roles
     * @param roles the list of roles
     */
    public void updateRoles(String userId, List<String> roles);

    /**
     * Updates the user's permissions to the given list of permissions
     * @param userId id of the user for which to update permissions
     * @param permissions the list of permissions
     */
    public void updatePermissions(String userId, List<String> permissions);

}
