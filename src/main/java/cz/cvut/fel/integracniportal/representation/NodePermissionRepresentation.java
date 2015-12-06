package cz.cvut.fel.integracniportal.representation;

import cz.cvut.fel.integracniportal.model.NodePermission;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eldar Iosip
 */
public class NodePermissionRepresentation {

    private NodePermission[] permissions;

    public NodePermission[] getPermissions() {
        return permissions;
    }

    public void setPermissions(NodePermission[] permissions) {
        this.permissions = permissions;
    }
}
