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
    public Set<AccessControlPermission> getAccessControlPermissionTypes();

    /**
     * Return the intersect of all group permissions where the userId is member in, merged with user permissions.
     *
     * @return Set<AccessControlPermission>
     */
    public Set<AccessControlPermission> getAccessControlPermissions(Long nodeId, Long userId);

    /**
     * Calculate ACE parent for selected node.
     *
     * @param node Node|null
     */
    public Node getAceParent(Node node);

    /**
     * When node is moved into another context, function updated the ACE parent reference for selected node and it's subnodes.
     *
     * @param node Node|null
     */
    public void updateAceParent(Node node, Node parent);

    public void updateNodeAceByUser(Long nodeId, Long userId, Set<AccessControlPermission> permissions);

    public void updateNodeAceByGroup(Long nodeId, Long groupId, Set<AccessControlPermission> permissions);

    public boolean userHasAcPermission(Long nodeId, Long userId, AccessControlPermission permission);

    public Set<Node> getSharedNodes(String spaceId, UserDetails currentUser);
}
