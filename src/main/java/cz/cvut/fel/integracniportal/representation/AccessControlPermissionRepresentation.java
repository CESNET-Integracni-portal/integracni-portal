package cz.cvut.fel.integracniportal.representation;

import cz.cvut.fel.integracniportal.model.AccessControlPermission;

import java.util.Set;

/**
 * @author Eldar Iosip
 */
public class AccessControlPermissionRepresentation {

    private Set<AccessControlPermission> permissions;

    public Set<AccessControlPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<AccessControlPermission> permissions) {
        this.permissions = permissions;
    }
}
