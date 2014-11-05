package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource class for user details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsRepresentation {

    private long userId;

    private String username;

    private String password;

    private List<String> userRoles = new ArrayList<String>();

    public UserDetailsRepresentation() {}
    public UserDetailsRepresentation(UserDetails userDetails) {
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

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }
    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }

}
