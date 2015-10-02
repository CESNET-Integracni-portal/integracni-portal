package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.AbstractNode;
import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.UserDetails;

/**
 * Provide CRUD operations to manage the FSNode permissions.
 *
 * @author Eldar Iosip
 */
public interface AclPermissionService {

    public AclPermission getUserPermissions(UserDetails owner, AbstractNode node);

    public AclPermission createUserPermissions(UserDetails owner, long permissions);

    public AclPermission createGroupPermissions(UserDetails group, long permissions);

    public AclPermission deletePermission(AclPermission permission, Long permissions);
}
