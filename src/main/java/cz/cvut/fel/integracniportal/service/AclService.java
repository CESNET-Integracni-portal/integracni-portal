package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.AccessControlPermission;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.Node;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.util.Set;

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
     * Return all the available AccessControlPermission objects.
     *
     * @return Set<AccessControlPermission>
     */
    public Set<AccessControlPermission> getAccessControlPermissions(Long nodeId, Long userId);

    /**
     * Calculate parent with ACE.
     *
     * @param node Node|null
     */
    public Node getAceParent(Node node);

    /**
     * @param node Node|null
     */
    public void updateAceParent(Node node, Node parent);

    /**
     * @param node Node|null
     */
    public void updateAceParent(Node node, Node parent, boolean hard);

    public void updateNodeAcpForUser(Long nodeId, Long userId, AccessControlPermission[] permissions);

    public void updateNodeAcpForGroup(Long nodeId, Long groupId, AccessControlPermission[] permissions);

    public boolean couldModifyAcl(Node node, UserDetails user);

    public boolean userHasAcPermission(Node node, UserDetails user, AccessControlPermission permission);
}
