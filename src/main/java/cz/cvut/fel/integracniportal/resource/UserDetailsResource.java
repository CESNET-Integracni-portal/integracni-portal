package cz.cvut.fel.integracniportal.resource;

import cz.cvut.fel.integracniportal.model.UserDetails;

/**
 * Resource class for user details.
 */
public class UserDetailsResource {

    private long userId;

    private String username;

    public UserDetailsResource() {}
    public UserDetailsResource(UserDetails userDetails) {
        this.userId = userDetails.getUserId();
        this.username = userDetails.getUsername();
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
