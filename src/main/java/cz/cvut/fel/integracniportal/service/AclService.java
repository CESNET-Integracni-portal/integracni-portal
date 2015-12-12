package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.AccessControlPermission;

/**
 * @author Eldar Iosip
 */
public interface AclService {

    /**
     * Return all the available AccessControlPermission objects.
     *
     * @return AccessControlPermission[]
     */
    public AccessControlPermission[] getAccessControlPermissionTypes();

    public void updateNodeAccessControlPermissions(Long nodeId, Long userId, AccessControlPermission[] permissions);

}
