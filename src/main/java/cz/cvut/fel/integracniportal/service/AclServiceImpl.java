package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AccessControlEntryDao;
import cz.cvut.fel.integracniportal.exceptions.AclDeniedAccessException;
import cz.cvut.fel.integracniportal.exceptions.NodeNotFoundException;
import cz.cvut.fel.integracniportal.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public void updateNodeAceByUser(Long nodeId, Long userId, Set<AccessControlPermission> permissions) {
        Node node = nodeService.getNodeById(nodeId);
        UserDetails currentUser = userDetailsService.getCurrentUser();

        //Check whether user could edit permissions for selected Node
        checkPermission(node, currentUser, AccessControlPermission.EDIT_PERMISSIONS);

        //Load ACE (if not present, create)
        UserDetails targetUser = userDetailsService.getUserById(userId);
        AccessControlEntry accessControlEntry = loadOrCreateUserAce(node, currentUser, targetUser);

        updateAcePermissions(node, accessControlEntry, permissions);

        //Become an acSubroot
        if (node.getAcParent() != null) {
            //Copy all ACE from acParent
            for (AccessControlEntry entry : node.getAcParent().getAcEntries()) {
                if (targetUser.equals(entry.getTargetUser())) {
                    //ignore parent ACE for targetUser
                    continue;
                }
                AccessControlEntry entryCopy = new AccessControlEntry();
                entryCopy.setTargetNode(node);
                entryCopy.setOwner(currentUser);
                entryCopy.setTargetUser(entry.getTargetUser());
                entryCopy.setTargetGroup(entry.getTargetGroup());
                entryCopy.getAccessControlPermissions().addAll(entry.getAccessControlPermissions());
                node.getAcEntries().add(entryCopy);
                accessControlEntryDao.save(entryCopy);
            }

            node.setRootParent((Folder) node.getAcParent());
            node.setAcParent(null);
        }

        updateTree(node, accessControlEntry);
    }

    @Override
    public void updateNodeAceByGroup(Long nodeId, Long groupId, Set<AccessControlPermission> permissions) {
        Node node = nodeService.getNodeById(nodeId);
        UserDetails currentUser = userDetailsService.getCurrentUser();

        //Check whether user could edit permissions for selected Node
        checkPermission(node, currentUser, AccessControlPermission.EDIT_PERMISSIONS);

        //Load ACE (if not present, create)
        Group targetGroup = groupService.getGroupById(groupId);
        AccessControlEntry accessControlEntry = loadOrCreateGroupAce(node, currentUser, targetGroup);

        updateAcePermissions(node, accessControlEntry, permissions);

        //Become an acSubroot
        if (node.getAcParent() != null) {
            //Copy all ACE from acParent
            for (AccessControlEntry entry : node.getAcParent().getAcEntries()) {
                if (targetGroup.equals(entry.getTargetGroup())) {
                    //ignore parent ACE for targetGroup
                    continue;
                }
                AccessControlEntry entryCopy = new AccessControlEntry();
                entryCopy.setTargetNode(node);
                entryCopy.setOwner(currentUser);
                entryCopy.setTargetUser(entry.getTargetUser());
                entryCopy.setTargetGroup(entry.getTargetGroup());
                entryCopy.getAccessControlPermissions().addAll(entry.getAccessControlPermissions());
                node.getAcEntries().add(entryCopy);
                accessControlEntryDao.save(entryCopy);
            }

            node.setRootParent((Folder) node.getAcParent());
            node.setAcParent(null);
        }

        updateTree(node, accessControlEntry);
    }

    private void updateTree(Node node, AccessControlEntry accessControlEntry) {
        if (node.getAcEntries().isEmpty()) {
            //Register the change to underlying Nodes
            if (node.getParent() == null) {
                updateAceParentAceRemove(node, null);
            } else {
                node.setAcParent(node.getRootParent());
                node.setRootParent(null);
                updateAceParentAceRemove(node, node.getAcParent());
            }
        } else {
            //Register the change to underlying Nodes
            updateSubnodes(node, accessControlEntry, node);
        }
    }

    private void updateSubnodes(Node node, AccessControlEntry entry, Node acParent) {
        for (Node subnode : node.getSubnodes()) {
            if (subnode.getAcParent() != null && !subnode.getAcParent().equals(acParent)) {
                //referenced to old acParent -> update to new
                subnode.setAcParent(acParent);
                updateSubnodes(subnode, entry, acParent);
            } else if (subnode.getAcParent() == null) {
                //merge entry for user
                boolean isFound = false;
                for (Iterator<AccessControlEntry> j = subnode.getAcEntries().iterator(); j.hasNext(); ) {
                    AccessControlEntry persistedEntry = j.next();
                    if ((entry.getTargetUser() != null && entry.getTargetUser().equals(persistedEntry.getTargetUser()))
                            || (entry.getTargetGroup() != null && (entry.getTargetGroup().equals(persistedEntry.getTargetGroup())))) {
                        isFound = true;
                        if (entry.getAccessControlPermissions().isEmpty()) {
                            j.remove();
                            accessControlEntryDao.delete(persistedEntry);
                        } else {
                            persistedEntry.getAccessControlPermissions().clear();
                            persistedEntry.getAccessControlPermissions().addAll(entry.getAccessControlPermissions());
                            accessControlEntryDao.save(persistedEntry);
                        }
                    }
                }

                if (subnode.getAcEntries().isEmpty()) {
                    subnode.setAcParent(node.getAcParent());
                } else if (!isFound && !entry.getAccessControlPermissions().isEmpty()) {
                    AccessControlEntry entryCopy = new AccessControlEntry();
                    entryCopy.setTargetNode(subnode);
                    entryCopy.setOwner(entry.getOwner());
                    entryCopy.setTargetUser(entry.getTargetUser());
                    entryCopy.setTargetGroup(entry.getTargetGroup());
                    entryCopy.getAccessControlPermissions().addAll(entry.getAccessControlPermissions());
                    subnode.getAcEntries().add(entryCopy);
                    accessControlEntryDao.save(entryCopy);
                }

                updateSubnodes(subnode, entry, subnode);
            } else {
                updateSubnodes(subnode, entry, acParent);
            }
        }

        if (node.getAcEntries().isEmpty()) {
            node.setAcParent(acParent);
            node.setRootParent(null);
        }
    }

    /**
     * If set is empty, remove ACE. Otherwise overwrite permissions and save.
     *
     * @param node        target
     * @param entry       loaded ACE
     * @param permissions newly created
     */
    private void updateAcePermissions(Node node, AccessControlEntry entry, Set<AccessControlPermission> permissions) {
        if (permissions.isEmpty()) {
            entry.getAccessControlPermissions().clear();
            //attached entity, remove from DB
            if (entry.getId() != null) {
                node.getAcEntries().remove(entry);
                accessControlEntryDao.delete(entry);
            }
        } else {
            //overwrite permissions and save
            if (!node.getAcEntries().contains(entry)) {
                node.getAcEntries().add(entry);
            }
            entry.getAccessControlPermissions().clear();
            entry.getAccessControlPermissions().addAll(permissions);
            accessControlEntryDao.save(entry);
        }
    }

    private List<AccessControlEntry> getInheritedAcEntries(Long targetUserId, Node targetNode) {
        if (targetNode.getAcParent() == null) {
            return accessControlEntryDao.getByTargetUserGroupsAndNode(targetUserId, targetNode.getId());
        } else {
            return accessControlEntryDao.getByTargetUserGroupsAndNode(targetUserId, targetNode.getId(), targetNode.getAcParent().getId());
        }
    }

    @Override
    public Set<Node> getSharedNodes(String spaceId, UserDetails currentUser) {
        List<AccessControlEntry> accessControlEntries = accessControlEntryDao.getByTargetUserNoOwnerPermission(
                currentUser.getId(),
                AccessControlPermission.READ
        );

        Set<Node> nodes = new HashSet<Node>();
        for (AccessControlEntry entry : accessControlEntries) {
            nodes.add(entry.getTargetNode());
        }

        return nodes;
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
    public Set<AccessControlPermission> getAccessControlPermissionTypes() {
        return new HashSet<AccessControlPermission>(Arrays.asList(AccessControlPermission.values()));
    }

    @Override
    //TODO: restrict access to users, with READ permission or to its owner
    public Set<AccessControlPermission> getAccessControlPermissions(Long nodeId, Long userId) {
        Node node = nodeService.getNodeById(nodeId);
        UserDetails currentUser = userDetailsService.getCurrentUser();

        //If user is requesting his own permissions => all of them
        if (currentUser.getId().equals(userId) && node.getOwner().getId().equals(userId)) {
            return this.getAccessControlPermissionTypes();
        }

        List<AccessControlEntry> accessControlEntries = this.getInheritedAcEntries(userId, node);

        Set<AccessControlPermission> groupPermissions = new HashSet<AccessControlPermission>();
        Set<AccessControlPermission> userPermissions = new HashSet<AccessControlPermission>();

        for (AccessControlEntry entry : accessControlEntries) {
            if (entry.getTargetGroup() != null) {
                //Group permissions
                if (groupPermissions.isEmpty()) {
                    //Trivial addition
                    groupPermissions.addAll(entry.getAccessControlPermissions());
                } else {
                    //Intersection with existing ones
                    groupPermissions.retainAll(entry.getAccessControlPermissions());
                }
            } else if (entry.getTargetUser() != null) {
                //User permissions
                userPermissions.addAll(entry.getAccessControlPermissions());
            }
        }

        //Permissions addressed to the user are the most valuable
        groupPermissions.addAll(userPermissions);
        return groupPermissions;
    }

    @Override
    public void updateAceParent(Node node, Node parent) {
        Node aceParent = this.getAceParent(parent);
        this.updateAceParentAceRemove(node, aceParent);
    }

    @Override
    public boolean userHasAcPermission(Long nodeId, Long userId, AccessControlPermission permission) {
        return this.getAccessControlPermissions(nodeId, userId).contains(permission);
    }

    /**
     * Clear all the subpermissions and set a new acParent for whole subtree.
     *
     * @param node      Node filesystem parent
     * @param aceParent Node new AC parent for subtree
     */
    private void updateAceParentAceRemove(Node node, Node aceParent) {
        node.setAcParent(aceParent);
        node.setRootParent(null);
        node.getAcEntries().clear();
        for (Node subnode : node.getSubnodes()) {
            updateAceParentAceRemove(subnode, this.getAceParent(subnode.getParent()));
        }
    }

    /**
     * Check whether node exists and user has permission to modify it's ACL.
     * DONE
     *
     * @param node Node target node
     * @param user UserDetails user who wants to modify target node ACL
     */
    private void checkPermission(Node node, UserDetails user, AccessControlPermission permission) {
        if (node == null) {
            throw new NodeNotFoundException("Node with requested ID was not found.");
        }

        if (!(this.userHasAcPermission(node.getId(), user.getId(), permission)
                || node.getOwner().equals(user))) {
            throw new AclDeniedAccessException("User has not permission to " + permission + " Node ACL");
        }
    }

    /**
     * Get an ACE instance for requested user. If no ACE stored, create new.
     *
     * @param node        target
     * @param currentUser owner of the new ACE
     * @param targetUser  for the ACE
     * @return AccessControlEntry
     */
    private AccessControlEntry loadOrCreateUserAce(Node node, UserDetails currentUser, UserDetails targetUser) {
        AccessControlEntry entry = accessControlEntryDao.getByTargetUserAndNode(targetUser.getId(), node.getId());

        if (entry == null) {
            entry = new AccessControlEntry();
            entry.setTargetNode(node);
            entry.setOwner(currentUser);
            entry.setTargetUser(targetUser);
        }

        return entry;
    }

    /**
     * Get an ACE instance for requested group. If no ACE stored, create new.
     *
     * @param node        target
     * @param currentUser owner of the new ACE
     * @param targetGroup for the ACE
     * @return AccessControlEntry
     */
    private AccessControlEntry loadOrCreateGroupAce(Node node, UserDetails currentUser, Group targetGroup) {
        AccessControlEntry entry = accessControlEntryDao.getByTargetGroupAndNode(targetGroup.getId(), node.getId());

        if (entry == null) {
            entry = new AccessControlEntry();
            entry.setTargetNode(node);
            entry.setOwner(currentUser);
            entry.setTargetGroup(targetGroup);
        }

        return entry;
    }
}
