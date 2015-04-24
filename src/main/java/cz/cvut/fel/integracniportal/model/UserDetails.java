package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Entity for user login credentials and list of assigned roles.
 */
@Entity
@Table(name = "user_details")
public class UserDetails extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "org_unit_id", nullable = true)
    private Long organizationalUnitId;

    @ManyToMany
    private List<UserRole> userRoles;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Permission> permissions;

    @OneToMany
    private List<Label> labels;

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public void setId(Long userId) {
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

    public Long getOrganizationalUnitId() {
        return organizationalUnitId;
    }

    public void setOrganizationalUnitId(Long organizationalUnitId) {
        this.organizationalUnitId = organizationalUnitId;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }
}
