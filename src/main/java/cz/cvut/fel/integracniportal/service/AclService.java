package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.NodePermission;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;

/**
 * @author Eldar Iosip
 */
public interface AclService {

    /**
     * Return all the available NodePermission objects.
     *
     * @return NodePermission[]
     */
    public NodePermission[] getNodePermissionTypes();

    public void updateNodePermissions(String fileuuid, Long userId, NodePermission[] permissions);

}
