package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AccessControlEntryDao;
import cz.cvut.fel.integracniportal.exceptions.AclDeniedAccessException;
import cz.cvut.fel.integracniportal.exceptions.GroupNotFoundException;
import cz.cvut.fel.integracniportal.exceptions.NodeNotFoundException;
import cz.cvut.fel.integracniportal.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void updateAceParent(Node node, Node parent, boolean hard) {
        Node aceParent = this.getAceParent(parent);

        if (hard) {
            this.updateAceParentAceRemove(node, aceParent);
        }
        //TODO else updateAceParentAcePreserve
    }

    @Override
    public void updateNodeAcpForUser(Long nodeId, Long userId, AccessControlPermission[] permissions) {
        Node node = nodeService.getNodeById(nodeId);
        UserDetails currentUser = userDetailsService.getCurrentUser();

        if (!this.couldModifyAcl(node, currentUser)) {
            throw new AclDeniedAccessException("User has not permission to update Node ACL");
        }

        List<AccessControlEntry> accessControlEntries = accessControlEntryDao.getByTargetUserAndNode(userId, nodeId);

        AccessControlEntry accessControlEntry = null;

        UserDetails targetUser = userDetailsService.getUserById(userId);
        for (AccessControlEntry entry : accessControlEntries) {
            if (entry.getTargetUser().equals(targetUser)) {
                accessControlEntry = entry;
                break;
            }
        }
        if (accessControlEntries.isEmpty() || accessControlEntry == null) {
            accessControlEntry = new AccessControlEntry();

            accessControlEntry.setTargetNode(node);
            accessControlEntry.setOwner(currentUser);
            accessControlEntry.setTargetUser(targetUser);
        }

        accessControlEntry.getAccessControlPermissions().clear();
        for (AccessControlPermission permission : permissions) {
            accessControlEntry.getAccessControlPermissions().add(permission);
        }

        if (accessControlEntry.getAccessControlPermissions().isEmpty()) {
            accessControlEntryDao.delete(accessControlEntry);
        } else {
            accessControlEntryDao.save(accessControlEntry);
        }
    }

    @Override
    public void updateNodeAcpForGroup(Long nodeId, Long groupId, AccessControlPermission[] permissions) {
        Node node = nodeService.getNodeById(nodeId);

        if (node == null) {
            throw new NodeNotFoundException("Node with requested ID was not found.");
        }

        UserDetails currentUser = userDetailsService.getCurrentUser();

        if (!this.couldModifyAcl(node, currentUser)) {
            throw new AclDeniedAccessException("User has not permission to update Node ACL");
        }

        Group targetGroup = groupService.getGroupById(groupId);

        if (targetGroup == null) {
            throw new GroupNotFoundException("Group with requested ID not found.");
        }

        AccessControlEntry accessControlEntry = accessControlEntryDao.getByTargetGroupAndNode(groupId, nodeId);

        if (accessControlEntry == null) {
            accessControlEntry = new AccessControlEntry();

            accessControlEntry.setTargetNode(node);
            accessControlEntry.setOwner(currentUser);
            accessControlEntry.setTargetGroup(targetGroup);
        }

        accessControlEntry.getAccessControlPermissions().clear();
        for (AccessControlPermission permission : permissions) {
            accessControlEntry.getAccessControlPermissions().add(permission);
        }

        if (accessControlEntry.getAccessControlPermissions().isEmpty()) {
            accessControlEntryDao.delete(accessControlEntry);
        } else {
            accessControlEntryDao.save(accessControlEntry);
        }
    }

    @Override
    public boolean couldModifyAcl(Node node, UserDetails user) {
        return this.userHasAcPermission(node, user, AccessControlPermission.EDIT_PERMISSIONS) || node.getOwner().equals(user);
    }

    @Override
    public boolean userHasAcPermission(Node node, UserDetails user, AccessControlPermission permission) {
        Set<AccessControlPermission> permissions = this.getAccessControlPermissions(node.getId(), user.getId());

        return permissions.contains(permission);
    }

    private void updateAceParentAceRemove(Node node, Node aceParent) {
        node.setAcParent(aceParent);
        for (Node subnode : node.getSubnodes()) {
            updateAceParentAceRemove(subnode, this.getAceParent(subnode.getParent()));
        }
    }
}
