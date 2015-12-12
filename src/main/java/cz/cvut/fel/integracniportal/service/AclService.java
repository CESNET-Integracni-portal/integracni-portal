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
    public AccessControlPermission[] getNodePermissionTypes();

    public void updateNodePermissions(Long fileId, Long userId, AccessControlPermission[] permissions);

    public void updateFolderNodePermissions(Long folderId, Long userId, AccessControlPermission[] permissions);

}
