package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.NodePermission;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.AbstractUserRepresentation;
import cz.cvut.fel.integracniportal.representation.AclPermissionRepresentation;
import cz.cvut.fel.integracniportal.representation.NodePermissionRepresentation;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provide operations to manage the AbstractNode permissions.
 *
 * @author Eldar Iosip
 */
public interface AclPermissionService {

    /**
     * Return all possible ENUM values for AclPermission objects.
     *
     * @return NodePermission[]
     */
    public NodePermission[] getAllPermissionTypes();

    /**
     * Return the set of permissions allowed to the user or group (AbstractUser).
     */
    public Set<NodePermission> getAclPermissions(String nodeId, Long userId);

    /**
     * Update the list of AclPermission object for particular Abstract Node.
     */
    public void updateNodePermissions(String nodeId, List<AclPermissionRepresentation> aclPermissionRepresentations);

    /**
     * Return whether user or group has the requested permission.
     * <p>
     * User permissions are more valuable than group ones.
     * At least one group should have permitted access to the node,
     * otherwise the user's permission should be used.
     * <p>
     * If group has a permission, but user is not defined, than automatically user is permitted.
     */
    public boolean hasPermission(String nodeId, Long userId, NodePermission permission);

    /**
     * Add permission for selected node and user.
     * <p>
     * If the AclPermission object does not exist, create it. Otherwise update existing one.
     */
    public void setPermission(String nodeId, Long userId, NodePermission permission);

    /**
     * Inherit parent ACL Permission from it's parent
     */
    public void inheritParentAcl(String nodeId);
}
