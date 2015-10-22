package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.NodePermission;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.AbstractUserRepresentation;
import cz.cvut.fel.integracniportal.representation.AclPermissionRepresentation;
import cz.cvut.fel.integracniportal.representation.NodePermissionRepresentation;

import java.util.List;
import java.util.Map;

/**
 * Provide operations to manage the AbstractNode permissions.

 * @author Eldar Iosip
 */
public interface AclPermissionService {

    public NodePermission[] getAllPermissionTypes();

    public List<NodePermission> getNodeAclForUser(String nodeId, Long userId);

    public void updateAclNodePermissions(String nodeId, List<AclPermissionRepresentation> aclPermissionRepresentations);
}
