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

    @Column(nullable = true)
    private String alfrescoUsername;

    @Column(nullable = true)
    private String alfrescoPassword;

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

    public String getAlfrescoUsername() {
        return alfrescoUsername;
    }
    public void setAlfrescoUsername(String alfrescoUsername) {
        this.alfrescoUsername = alfrescoUsername;
    }

    public String getAlfrescoPassword() {
        return alfrescoPassword;
    }
    public void setAlfrescoPassword(String alfrescoPassword) {
        this.alfrescoPassword = alfrescoPassword;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }
    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
}
