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

    public NodePermission[] getAllPermissionTypes();

    public Set<NodePermission> getNodeAclForUser(String nodeId, Long userId);

    public void updateNodePermissions(String nodeId, List<AclPermissionRepresentation> aclPermissionRepresentations);

    /**
     * Return whether user or group has the requested permission.
     *
     * User permissions are more valuable than group ones.
     *
     * @param nodeId
     * @param userId
     * @param permission
     * @return
     */
    public boolean hasPermission(String nodeId, Long userId, NodePermission permission);

    public void setPermission(String nodeId, Long userId, NodePermission permission);

    /**
     * Inherit parent ACL Permission from it's parent
     * @param nodeId
     */
    public void inheritParentAcl(String nodeId);
}
