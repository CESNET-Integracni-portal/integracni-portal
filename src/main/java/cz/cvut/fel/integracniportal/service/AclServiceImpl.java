package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AccessControlEntryDao;
import cz.cvut.fel.integracniportal.exceptions.AclDeniedAccessException;
import cz.cvut.fel.integracniportal.exceptions.GroupNotFoundException;
import cz.cvut.fel.integracniportal.exceptions.NodeNotFoundException;
import cz.cvut.fel.integracniportal.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eldar Iosip
 */
@Service
@Transactional
public class AclServiceImpl implements AclService {

    @Autowired
    private NodeService nodeService;

    @Autowired
    GroupService groupService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AccessControlEntryDao accessControlEntryDao;

    @Override
    public AccessControlPermission[] getAccessControlPermissionTypes() {
        return AccessControlPermission.values();
    }

    @Override
    public Set<AccessControlPermission> getAccessControlPermissions(Long nodeId, Long userId) {
        Node node = nodeService.getNodeById(nodeId);
        UserDetails currentUser = userDetailsService.getCurrentUser();

        //If user is requesting his own permissions => all of them
        if (currentUser.getId().equals(userId) && node.getOwner().getId().equals(userId)) {
            return new HashSet<AccessControlPermission>(Arrays.asList(this.getAccessControlPermissionTypes()));
        }

        List<AccessControlEntry> accessControlEntries = accessControlEntryDao.getByTargetUserAndNode(userId, nodeId);

        Set<AccessControlPermission> groupPermissions = new HashSet<AccessControlPermission>();
        Set<AccessControlPermission> userPermissions = new HashSet<AccessControlPermission>();

        for (AccessControlEntry entry : accessControlEntries) {
            if (entry.getTargetGroup() != null) {
                //Group permissions
                if (groupPermissions.isEmpty()) {
                    groupPermissions.addAll(entry.getAccessControlPermissions());
                } else {
                    groupPermissions.retainAll(entry.getAccessControlPermissions());
                }
            } else if (entry.getTargetUser() != null) {
                //User permissions
                userPermissions.addAll(entry.getAccessControlPermissions());
            }
        }

        groupPermissions.addAll(userPermissions);
        return groupPermissions;
    }

    @Override
    public Node getAceParent(Node parent) {
        //For root Nodes
        if (parent == null) {
            return null;
        }

        //For inner (1st level) Nodes
        if (parent.getAcParent() == null) {
            return parent;
        }

        return parent.getAcParent();
    }

    @Override
    public void updateAceParent(Node node, Node parent) {
        this.updateAceParent(node, parent, true);
    }


    @Override
    public void updateNodeAcpForUser(Long nodeId, Long userId, AccessControlPermission[] permissions) {
        Node node = nodeService.getNodeById(nodeId);
        UserDetails currentUser = userDetailsService.getCurrentUser();

        this.checkPermissionToModify(node, currentUser);

        //Update process
        List<AccessControlEntry> accessControlEntries = accessControlEntryDao.getByTargetUserAndNode(userId, nodeId);

        AccessControlEntry accessControlEntry = null;

        //Store ACP as targetUser, not targetGroup
        UserDetails targetUser = userDetailsService.getUserById(userId);
        for (AccessControlEntry entry : accessControlEntries) {
            if (entry.getTargetUser().equals(targetUser)) {
                accessControlEntry = entry;
                break;
            }
        }

        //If first ACE entry or all other entries are targeted on group he is in
        //create ACE with targetUser
        if (accessControlEntries.isEmpty() || accessControlEntry == null) {
            accessControlEntry = new AccessControlEntry();

            accessControlEntry.setTargetNode(node);
            accessControlEntry.setOwner(currentUser);
            accessControlEntry.setTargetUser(targetUser);
        }

        //Override strored ACP
        accessControlEntry.getAccessControlPermissions().clear();
        for (AccessControlPermission permission : permissions) {
            accessControlEntry.getAccessControlPermissions().add(permission);
        }

        //TODO: check in first, if it is acSubnode
        if (accessControlEntry.getAccessControlPermissions().isEmpty()) {
            accessControlEntryDao.delete(accessControlEntry);
        } else {
            accessControlEntryDao.save(accessControlEntry);
        }
    }

    @Override
    public void updateNodeAcpForGroup(Long nodeId, Long groupId, AccessControlPermission[] permissions) {
        Node node = nodeService.getNodeById(nodeId);
        UserDetails currentUser = userDetailsService.getCurrentUser();

        this.checkPermissionToModify(node, currentUser);

        //Get targerGroup and check null
        Group targetGroup = groupService.getGroupById(groupId);
        if (targetGroup == null) {
            throw new GroupNotFoundException("Group with requested ID not found.");
        }

        //Update process
        AccessControlEntry accessControlEntry = accessControlEntryDao.getByTargetGroupAndNode(groupId, nodeId);

        //If Node has no ACE for targetGroup, create new
        if (accessControlEntry == null) {
            accessControlEntry = new AccessControlEntry();

            accessControlEntry.setTargetNode(node);
            accessControlEntry.setOwner(currentUser);
            accessControlEntry.setTargetGroup(targetGroup);
        }

        //Override previously stored ACPs
        accessControlEntry.getAccessControlPermissions().clear();
        for (AccessControlPermission permission : permissions) {
            accessControlEntry.getAccessControlPermissions().add(permission);
        }

        //TODO: check in first, if it is acSubnode
        if (accessControlEntry.getAccessControlPermissions().isEmpty()) {
            accessControlEntryDao.delete(accessControlEntry);
        } else {
            accessControlEntryDao.save(accessControlEntry);
        }
    }

    @Override
    public boolean userHasAcPermission(Long nodeId, Long userId, AccessControlPermission permission) {
        return this.getAccessControlPermissions(nodeId, userId).contains(permission);
    }

    @Override
    public Set<Node> getSharedNodes(String spaceId, UserDetails currentUser) {
        List<AccessControlEntry> accessControlEntries = accessControlEntryDao.getByTargetUserNoOwnerPermission(currentUser.getId(), AccessControlPermission.READ);

        Set<Node> nodes = new HashSet<Node>();
        for (AccessControlEntry entry : accessControlEntries) {
            nodes.add(entry.getTargetNode());
        }

        return nodes;
    }

    private void updateAceParent(Node node, Node parent, boolean hard) {
        Node aceParent = this.getAceParent(parent);

        if (hard) {
            this.updateAceParentAceRemove(node, aceParent);
        }
        //TODO else updateAceParentAcePreserve
    }

    /**
     * Clear all the subpermissions and set a new acParent for whole subtree.
     *
     * @param node      Node filesystem parent
     * @param aceParent Node new AC parent for subtree
     */
    private void updateAceParentAceRemove(Node node, Node aceParent) {
        node.setAcParent(aceParent);
        node.getAcEntries().clear();
        for (Node subnode : node.getSubnodes()) {
            updateAceParentAceRemove(subnode, this.getAceParent(subnode.getParent()));
        }
    }

    /**
     * Check whether node exists and user has permission to modify it's ACL.
     *
     * @param node Node target node
     * @param user UserDetails user who wants to modify target node ACL
     */
    private void checkPermissionToModify(Node node, UserDetails user) {
        if (node == null) {
            throw new NodeNotFoundException("Node with requested ID was not found.");
        }

        if (!(this.userHasAcPermission(node.getId(), user.getId(), AccessControlPermission.EDIT_PERMISSIONS)
                || node.getOwner().equals(user))) {
            throw new AclDeniedAccessException("User has not permission to update Node ACL");
        }
    }

}
