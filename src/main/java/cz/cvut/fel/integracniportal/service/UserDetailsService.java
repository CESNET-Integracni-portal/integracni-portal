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
    public UserDetails getUserById(long userId);

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
    public List<UserDetails> getAllUsersInOrganizationalUnit(Long organizationalUnitId);

    /**
     * Creates a user from supplied {@link cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation}.
     *
     * @param userDetailsResource the user details
     * @return the user details
     */
    public UserDetails createUser(UserDetailsRepresentation userDetailsResource);

    /**
     * Updates a user from supplied {@link cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation}.
     *
     * @param userId              id of the user to be updated
     * @param userDetailsResource the user details
     * @return the user details
     */
    public UserDetails updateUser(Long userId, UserDetailsRepresentation userDetailsResource);

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

}
