package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.util.List;

/**
 * Provide CRUD operations to manage the FSNode permissions.
 * <p>
 * TODO: check if currentUser has permissions for this action or he is the node owner
 *
 * @author Eldar Iosip
 */
public interface AclPermissionService {


    public List<AclPermission> getNodeAcl(String nodeId);

    public AclPermission getNodeAclForUser(String nodeId, Long userId);

    public AclPermission getNodeAclForGroup(String nodeId, Long groupId);

    public void setNodeAclForUser(String nodeId, UserDetails user);

    public void setNodeAclForGroup(String nodeId, Long groupId);

    public void setNodeAcl(String nodeId, Long groupId);

    public void createUserPermissions(UserDetails owner, long permissions);

    public void createGroupPermissions(UserDetails group, long permissions);

    public void deletePermission(AclPermission permission, Long permissions);
}
