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

    public void updateNodePermissions(String fileuuid, Long userId, AccessControlPermission[] permissions);

    public void updateFolderNodePermissions(Long folderId, Long userId, AccessControlPermission[] permissions);

    public void updateGroupNodePermissions(String fileuuid, Long groupId, AccessControlPermission[] permissions);

    public void updateGroupFolderNodePermissions(Long folderId, Long groupId, AccessControlPermission[] permissions);

}
