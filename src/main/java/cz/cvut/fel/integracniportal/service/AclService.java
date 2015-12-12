package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.AccessControlPermission;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.Node;

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

    /**
     * If folder lacks the acParent, it becomes the acParent.
     * Otherwise the reference to acParent returned
     *
     * @param  node Node|null
     * @return AC Parent Folder
     */
    public Node getAcParent(Node node);

    public void updateNodeAccessControlPermissions(Long nodeId, Long userId, AccessControlPermission[] permissions);

}
