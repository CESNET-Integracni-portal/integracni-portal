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

    /**
     * Update ACE record for selected Node (user), may be propageted down the tree.
     *
     * @param nodeId      target
     * @param userId      target
     * @param permissions list
     */
    public void updateNodeAceByUser(Long nodeId, Long userId, Set<AccessControlPermission> permissions);

    /**
     * Update ACE record for selected Node (group), may be propageted down the tree.
     *
     * @param nodeId      target
     * @param groupId     target
     * @param permissions list
     */
    public void updateNodeAceByGroup(Long nodeId, Long groupId, Set<AccessControlPermission> permissions);

    /**
     * Check if user has an ACP.
     *
     * @param nodeId     target node
     * @param userId     to check
     * @param permission to check
     * @return true if has
     */
    public boolean userHasAcPermission(Long nodeId, Long userId, AccessControlPermission permission);

    /**
     * Return a set of shared nodes.
     *
     * @param spaceId     for space
     * @param currentUser by user
     * @return set of nodes or empty set, when no records found
     */
    public Set<Node> getSharedNodes(String spaceId, UserDetails currentUser);
}
