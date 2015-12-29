package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AccessControlEntryDao;
import cz.cvut.fel.integracniportal.exceptions.AclDeniedAccessException;
import cz.cvut.fel.integracniportal.exceptions.GroupNotFoundException;
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
    public void updateNodeAcPermissionsByUser(Long nodeId, Long userId, Set<AccessControlPermission> permissions) {
        //Prepare data for processing
        Node node = nodeService.getNodeById(nodeId);
        UserDetails currentUser = userDetailsService.getCurrentUser();
        UserDetails targetUser = userDetailsService.getUserById(userId);

        //Check whether user could edit permissions for selected Node
        this.checkPermission(node, currentUser, AccessControlPermission.EDIT_PERMISSIONS);

        //Load existing or create new ACE
        AccessControlEntry accessControlEntry = this.createOrLoadTheUserAce(node, currentUser, targetUser);

        this.updateAcPermissions(node, accessControlEntry, permissions);

        if (node.getAcParent() != null) {
            //If it's not a acSubroot -> make one

            //Copy all the ACEntries from the acParent.
            for (AccessControlEntry entry : node.getAcParent().getAcEntries()) {
                if (targetUser.equals(entry.getTargetUser())) {
                    continue; //if we added an ACE for targetUser in previous steps
                }
                AccessControlEntry entry1 = new AccessControlEntry();
                entry1.setTargetNode(node);
                entry1.setOwner(currentUser);
                entry1.setTargetUser(targetUser);
                entry1.setTargetGroup(entry.getTargetGroup());
                entry1.getAccessControlPermissions().addAll(entry.getAccessControlPermissions());
                node.getAcEntries().add(entry1);
                accessControlEntryDao.save(entry1);
            }

            //Now, Node is subscriber to his parent changes via rootParent reference
            node.setRootParent((Folder) node.getAcParent());
            Long oldAcParentId = node.getAcParent().getId();
            node.setAcParent(null);

            //Register the change to underlying Nodes
            updateAcParentForSubnodes(node, node, oldAcParentId);
        }

        //Send by recursion the new ACE, so they can process the rule and optionally restrict theirs state
        //or initiate the homomorphism to the parents
        copyAcEntriesToAcSubnodes(node, accessControlEntry);
    }

    @Override
    public void updateNodeAcpForGroup(Long nodeId, Long groupId, Set<AccessControlPermission> permissions) {
        //Prepare data for processing
        Node node = nodeService.getNodeById(nodeId);
        UserDetails currentUser = userDetailsService.getCurrentUser();
        Group targetGroup = groupService.getGroupById(groupId);

        //Check whether user could edit permissions for selected Node
        this.checkPermission(node, currentUser, AccessControlPermission.EDIT_PERMISSIONS);

        //Load existing or create new ACE
        AccessControlEntry accessControlEntry = this.createOrLoadTheGroupAce(node, currentUser, targetGroup);

        this.updateAcPermissions(node, accessControlEntry, permissions);

        if (node.getAcParent() != null) {
            //If it's not a acSubroot -> make one

            //Copy all the ACEntries from the acParent.
            for (AccessControlEntry entry : node.getAcParent().getAcEntries()) {
                if (targetGroup.equals(entry.getTargetGroup())) {
                    continue; //if we added an ACE for targetUser in previous steps
                }
                AccessControlEntry entry1 = new AccessControlEntry();
                entry1.setTargetNode(node);
                entry1.setOwner(currentUser);
                entry1.setTargetGroup(targetGroup);
                entry1.setTargetUser(entry.getTargetUser());
                entry1.getAccessControlPermissions().addAll(entry.getAccessControlPermissions());
                node.getAcEntries().add(entry1);
                accessControlEntryDao.save(entry1);
            }

            //Now, Node is subscriber to his parent changes via rootParent reference
            node.setRootParent((Folder) node.getAcParent());
            Long oldAcParentId = node.getAcParent().getId();
            node.setAcParent(null);

            //Register the change to underlying Nodes
            updateAcParentForSubnodes(node, node, oldAcParentId);
        }

        //Send by recursion the new ACE, so they can process the rule and optionally restrict theirs state
        //or initiate the homomorphism to the parents
        copyAcEntriesToAcSubnodes(node, accessControlEntry);
    }

    private AccessControlEntry createOrLoadTheUserAce(Node node, UserDetails currentUser, UserDetails targetUser) {
        //Get all entries (could be groups also)
        List<AccessControlEntry> accessControlEntries = accessControlEntryDao.getByTargetUserAndNode(
                targetUser.getId(),
                node.getId()
        );

        //If record is already exists for targetUser
        for (AccessControlEntry entry : accessControlEntries) {
            if (entry.getTargetUser().equals(targetUser) && entry.getTargetNode().equals(node)) {
                return entry;
            }
        }

        AccessControlEntry entry = new AccessControlEntry();
        entry.setTargetNode(node);
        entry.setOwner(currentUser);
        entry.setTargetUser(targetUser);

        return entry;
    }

    private AccessControlEntry createOrLoadTheGroupAce(Node node, UserDetails currentUser, Group targetGroup) {
        AccessControlEntry entry = accessControlEntryDao.getByTargetGroupAndNode(
                targetGroup.getId(),
                node.getId()
        );

        if (entry == null) {
            entry = new AccessControlEntry();
            entry.setTargetNode(node);
            entry.setOwner(currentUser);
            entry.setTargetGroup(targetGroup);
        }

        return entry;
    }

    private void updateAcPermissions(Node node, AccessControlEntry entry, Set<AccessControlPermission> permissions) {
        if (permissions.isEmpty()) {
            //If it's removal, and it's not a detached entity, remove from DB
            if (entry.getId() != null) {
                node.getAcEntries().remove(entry);
                accessControlEntryDao.delete(entry);
            }
        } else {
            //Update ACPermissions and save
            if (!node.getAcEntries().contains(entry)) {
                node.getAcEntries().add(entry);
            }
            entry.getAccessControlPermissions().clear();
            entry.getAccessControlPermissions().addAll(permissions);
            accessControlEntryDao.save(entry);
        }
    }

    /**
     * Initial instance of the node holds the new ACEntry with the new list of ACP (could be empty).
     * <p>
     * Function cycles in recursion through all acSubnodes and if the targetUser found, ACPermissions are rewritten
     * by the new combination. Otherwise it could be deleted, if the ACE permissions are empty.
     *
     * @param node  or acSubnode for faster updating the ACL
     * @param entry newly created ACE
     */
    private void copyAcEntriesToAcSubnodes(Node node, AccessControlEntry entry) {
        for (Node acSubnode : node.getAcSubnodes()) {
            for (Iterator<AccessControlEntry> j = acSubnode.getAcEntries().iterator(); j.hasNext(); ) {
                AccessControlEntry persistedEntry = j.next();
                if (entry.getTargetUser().equals(persistedEntry.getTargetUser())
                        || entry.getTargetGroup().equals(persistedEntry.getTargetGroup())) {
                    if (entry.getAccessControlPermissions().isEmpty()) {
                        j.remove();
                        accessControlEntryDao.delete(persistedEntry);
                    } else {
                        persistedEntry.getAccessControlPermissions().clear();
                        persistedEntry.getAccessControlPermissions().addAll(entry.getAccessControlPermissions());
                        accessControlEntryDao.save(persistedEntry);
                    }
                    break;
                }
            }

            this.copyAcEntriesToAcSubnodes(acSubnode, entry);
        }

        //If the structure is the same as acRoot, reference it as acParent (simplify next search call)
        if (node.getAcSubnodes().isEmpty() && node.getAcEntries().isEmpty()) {
            node.setAcParent(node.getRootParent());
            node.setRootParent(null);

            this.updateAcParentForSubnodes(node, node.getAcParent(), node.getId());
        }
    }

    /**
     * Change the acParent reference to the newly created one.
     *
     * @param node          current node
     * @param newAcParent   parent to newly reference to
     * @param oldAcParentId old parent for search purposes
     */
    private void updateAcParentForSubnodes(Node node, Node newAcParent, Long oldAcParentId) {
        for (Node subnode : node.getSubnodes()) {
            if (subnode.getAcParent().getId().equals(oldAcParentId)) {
                subnode.setAcParent(newAcParent);
                this.updateAcParentForSubnodes(subnode, newAcParent, oldAcParentId);
            }
        }
    }


    private List<AccessControlEntry> getInheritedAcEntries(Long targetUserId, Node targetNode) {
        if (targetNode.getAcParent() == null) {
            return accessControlEntryDao.getByTargetUserAndNode(targetUserId, targetNode.getId());
        } else {
            return accessControlEntryDao.getByTargetUserAndNode(targetUserId, targetNode.getId(), targetNode.getAcParent().getId());
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
        this.updateAceParent(node, parent, true);
    }

    private void updateAceParent(Node node, Node parent, boolean hard) {
        Node aceParent = this.getAceParent(parent);

        if (hard) {
            this.updateAceParentAceRemove(node, aceParent);
        } else {
            this.updateAcParentAcePreserve(node, aceParent);
        }
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
     * @param node      Node filesystem parent
     * @param aceParent Node new AC parent for subtree
     */
    private void updateAcParentAcePreserve(Node node, Node aceParent) {
        node.setAcParent(aceParent);
        for (Node subnode : node.getSubnodes()) {
            updateAcParentAcePreserve(subnode, this.getAceParent(subnode.getParent()));
        }
    }

    /**
     * Check whether node exists and user has permission to modify it's ACL.
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

    @Override
    public boolean userHasAcPermission(Long nodeId, Long userId, AccessControlPermission permission) {
        return this.getAccessControlPermissions(nodeId, userId).contains(permission);
    }

}
