package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.model.UserRole;

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

    private List<String> userRoles;

    public UserDetailsRepresentation() {}
    public UserDetailsRepresentation(UserDetails userDetails) {
        this.userId = userDetails.getUserId();
        this.username = userDetails.getUsername();
        List<String> roles =  new ArrayList<String>();
        for (UserRole role: userDetails.getUserRoles()) {
            roles.add(role.getName());
        }
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
