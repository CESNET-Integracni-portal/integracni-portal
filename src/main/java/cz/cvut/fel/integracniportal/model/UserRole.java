package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.Set;

/**
 * Entity for user roles used in Spring authorisation.
 */
@Entity
@Table(name = "user_role")
public class UserRole extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    @Column(name = "user_role_id")
    private Long userRoleId;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Permission> permissions;

    @Override
    public Long getId() {
        return userRoleId;
    }

    @Override
    public void setId(Long userRoleId) {
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

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
