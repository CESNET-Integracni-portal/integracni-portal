package cz.cvut.fel.integracniportal.representation;

import cz.cvut.fel.integracniportal.model.AccessControlPermission;

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
}
