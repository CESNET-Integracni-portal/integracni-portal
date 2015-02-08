package cz.cvut.fel.integracniportal.representation;

import cz.cvut.fel.integracniportal.model.Permission;
import cz.cvut.fel.integracniportal.model.UserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation class for user roles.
 */
public class UserRoleRepresentation {

    private Long id;

    private String name;

    private String description;

    private List<Permission> permissions = new ArrayList<Permission>();

    public UserRoleRepresentation() {
    }

    public UserRoleRepresentation(UserRole userRole) {
        this.id = userRole.getId();
        this.name = userRole.getName();
        this.description = userRole.getDescription();
        this.permissions.addAll(userRole.getPermissions());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

}
