package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.List;

/**
 * Entity for user login credentials and list of assigned roles.
 */
@Entity
@Table(name = "user_details")
public class UserDetails {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private long userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany
    private List<UserRole> userRoles;


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

    public List<UserRole> getUserRoles() {
        return userRoles;
    }
    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
}
