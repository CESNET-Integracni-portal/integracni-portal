package cz.cvut.fel.integracniportal.representation;

import cz.cvut.fel.integracniportal.model.AccessControlPermission;

import java.util.Set;

/**
 * @author Eldar Iosip
 */
public class AccessControlPermissionRepresentation {

    private AccessControlPermission[] permissions;

    public AccessControlPermission[] getPermissions() {
        return permissions;
    }

    public void setPermissions(AccessControlPermission[] permissions) {
        this.permissions = permissions;
    }

    public void setPermissionsFromSet(Set<AccessControlPermission> permissions) {
        this.permissions = permissions.toArray(new AccessControlPermission[permissions.size()]);
    }
}
