package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.UserDetails;

import java.util.List;

/**
 * Data Access Object interface for UserDetails login credentials.
 */
public interface UserDetailsDao {

    /**
     * Loads a reference to a {@link cz.cvut.fel.integracniportal.model.UserDetails}
     * @param userId Id of the user.
     * @return
     */
    public UserDetails getReference(String userId);

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
     * Saves the user into database.
     *
     * @param user UserDetails login credentials which are to be saved.
     */
    public void save(UserDetails user);

    /**
     * Removes the user from database.
     *
     * @param user UserDetails login credentials which are to be removed.
     */
    public void delete(UserDetails user);

    /**
     * Returns true if username already exists.
     * @param username the username
     * @return
     */
    public boolean usernameExists(String username);

    /**
     * Returns true if email already exists.
     * @param email the email
     * @return
     */
    public boolean emailExists(String email);

}
