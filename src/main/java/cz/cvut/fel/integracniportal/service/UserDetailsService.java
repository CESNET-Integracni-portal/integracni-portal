package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.UserDetails;

/**
 * Service for UserDetails login credentials.
 */
public interface UserDetailsService {

    /**
     * Finds a user in database by his id.
     * @param userId    Id of the user.
     * @return UserDetails login credentials.
     */
    public UserDetails getUserById(long userId);

    /**
     * Finds a user in database by his username.
     * @param username    Username of the user.
     * @return UserDetails login credentials.
     */
    public UserDetails getUserByUsername(String username);

    /**
     * Saves the user into database.
     * @param user    UserDetails login credentials which are to be saved.
     */
    public void saveUser(UserDetails user);

    /**
     * Removes the user from database.
     * @param user  UserDetails login credentials which are to be removed.
     */
    public void removeUser(UserDetails user);
}
