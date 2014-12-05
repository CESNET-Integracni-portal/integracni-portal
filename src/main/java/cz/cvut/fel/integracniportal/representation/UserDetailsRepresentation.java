package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.Permission;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.model.UserRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation class for user details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsRepresentation {

    private Long id;

    private String username;

    private String password;

    private Long unitId;

    private List<String> roles = new ArrayList<String>();

    private Map<String, Boolean> permissions = new HashMap<String, Boolean>();

    private List<String> directPermissions = new ArrayList<String>();

    public UserDetailsRepresentation() {}
    public UserDetailsRepresentation(UserDetails userDetails) {
        this(userDetails, true);
    }
    public UserDetailsRepresentation(UserDetails userDetails, boolean withRoles) {
        this.id = userDetails.getUserId();
        this.username = userDetails.getUsername();
        this.unitId = userDetails.getOrganizationalUnitId();
        if (withRoles) {
            for (Permission permission : Permission.values()) {
                permissions.put(permission.toString(), false);
            }

            if (userDetails.getPermissions() != null) {
                for (Permission permission: userDetails.getPermissions()) {
                    directPermissions.add(permission.toString());
                    permissions.put(permission.toString(), true);
                }
            }

            if (userDetails.getUserRoles() != null) {
                for (UserRole role : userDetails.getUserRoles()) {
                    roles.add(role.getName());
                    for (Permission permission : role.getPermissions()) {
                        permissions.put(permission.toString(), true);
                    }
                }
            }
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    public Long getUnitId() {
        return unitId;
    }
    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<String> getRoles() {
        return roles;
    }
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }
    public void setPermissions(Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    public List<String> getDirectPermissions() {
        return directPermissions;
    }
    public void setDirectPermissions(List<String> directPermissions) {
        this.directPermissions = directPermissions;
    }
}
