package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;

/**
 * Entity for user roles used in Spring authorisation.
 */
@Entity
@Table(name = "user_role")
public class UserRole {

    @Id
    @GeneratedValue
    @Column(name = "user_role_id")
    private long userRoleId;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    public long getUserRoleId() {
        return userRoleId;
    }
    public void setUserRoleId(long userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
