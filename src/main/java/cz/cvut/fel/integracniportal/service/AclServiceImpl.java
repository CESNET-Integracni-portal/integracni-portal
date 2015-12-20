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
        Node node = nodeService.getNodeById(nodeId);
        UserDetails currentUser = userDetailsService.getCurrentUser();
        this.checkPermission(node, currentUser, AccessControlPermission.EDIT_PERMISSIONS);

        UserDetails targetUser = userDetailsService.getUserById(userId);
        AccessControlEntry accessControlEntry = null;
        if (node.getAcParent() == null) {
            //Upravuju slozky ve space rootu, ktere nemaji zadne zavislosti na predkovi

            //Vytahnu vsechny zaznamy o pravech
            List<AccessControlEntry> accessControlEntries = accessControlEntryDao.getByTargetUserAndNode(userId, node.getId());

            //Pokud uz existuje zaznam pro konkretniho uzivatele a Node, tak jenom upravim
            for (AccessControlEntry entry : accessControlEntries) {
                if (entry.getTargetUser().equals(targetUser) && entry.getTargetNode().equals(node)) {
                    accessControlEntry = entry;
                    accessControlEntry.getAccessControlPermissions().clear();
                    break;
                }
            }

            //Pokud neexistuje, tak ho vytvorim a pridam jako noveho do nodu
            if (accessControlEntry == null) {
                accessControlEntry = new AccessControlEntry();
                accessControlEntry.setTargetNode(node);
                accessControlEntry.setOwner(currentUser);
                accessControlEntry.setTargetUser(targetUser);
            }

            //Nastavim nova prava a ulozim je
            accessControlEntry.getAccessControlPermissions().addAll(permissions);

            if (accessControlEntry.getAccessControlPermissions().isEmpty()) {
                accessControlEntryDao.delete(accessControlEntry);
            } else {
                accessControlEntryDao.save(accessControlEntry);
            }

            //Poslu vsem acSubnodum nove pravidlo, ktere maji zmergovat u sebe a u jejich acSubnoodu (rekurzivne)
            copyAcEntriesToAcSubnodes(node, accessControlEntry);
        } else {
            //Upravuju slozky, ktere jsou na prvni urovni a hloubeji

            //Vytahnu vsechny zaznamy o pravech upravovaneho Nodu
            List<AccessControlEntry> accessControlEntries = accessControlEntryDao.getByTargetUserAndNode(userId, node.getId());

            //Pokud uz existuje zaznam pro konkretniho uzivatele a Node, tak jenom upravim
            for (AccessControlEntry entry : accessControlEntries) {
                if (entry.getTargetUser().equals(targetUser) && entry.getTargetNode().equals(node)) {
                    accessControlEntry = entry;
                    accessControlEntry.getAccessControlPermissions().clear();
                    break;
                }
            }

            //Pokud neexistuje, tak ho vytvorim a pridam jako noveho do nodu
            if (accessControlEntry == null) {
                accessControlEntry = new AccessControlEntry();
                accessControlEntry.setTargetNode(node);
                accessControlEntry.setOwner(currentUser);
                accessControlEntry.setTargetUser(targetUser);
            }

            //Nastavim nova prava a ulozim je
            accessControlEntry.getAccessControlPermissions().addAll(permissions);

            if (accessControlEntry.getAccessControlPermissions().isEmpty()) {
                accessControlEntryDao.delete(accessControlEntry);
            } else {
                accessControlEntryDao.save(accessControlEntry);
            }

            //Pokud node ma nastaveneho acParenta, tak okopiruju z acParenta vsechna pravidla (asi az na ty, co jsou jine, nez ted ma (dle targetUsera)),
            //a pro vsechny potomky, node se stane novym AcParentem
            for (AccessControlEntry entry : node.getAcParent().getAcEntries()) {
                if (targetUser.equals(entry.getTargetUser())) {
                    continue;
                }
                AccessControlEntry entry1 = new AccessControlEntry();
                entry1.setTargetNode(node);
                entry1.setOwner(currentUser);
                entry1.setTargetUser(targetUser);
                entry1.getAccessControlPermissions().addAll(entry.getAccessControlPermissions());
                node.getAcEntries().add(entry1);
                accessControlEntryDao.save(entry1);
            }

            node.setRootParent((Folder) node.getAcParent());
            Long oldAcParentId = node.getAcParent().getId();
            node.setAcParent(null);

            updateAcParentForSubnodes(node, node, oldAcParentId);

            //Poslu vsem acSubnodum nove pravidlo, ktere maji zmergovat u sebe a u jejich acSubnoodu (rekurzivne)
            copyAcEntriesToAcSubnodes(node, accessControlEntry);
        }
    }

    /**
     * Tady dostanu node, do ktereho jsem pridal nove pravidlo a druhym argumentem je seznam vsech jeho povoleni.
     * <p>
     * Mam za ukol projit vsechny jeho acSubnodes a pridat jim nova pravidla, pokud zjistim, ze pravidlo pro konkretniho
     * uzivatele jiz existuje v acSubnodu, tak ho zcela prepisu.
     *
     * @param node
     * @param entry
     */
    private void copyAcEntriesToAcSubnodes(Node node, AccessControlEntry entry) {
        for (Node acSubnode : node.getAcSubnodes()) {
            for (Iterator<AccessControlEntry> j = acSubnode.getAcEntries().iterator(); j.hasNext(); ) {
                AccessControlEntry persistedEntry = j.next();
                if (entry.getTargetUser().equals(persistedEntry.getTargetUser())) {
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
    }

    /**
     * Tady bych mel nastavit noveho acParenta vsem subnoodum, ktere jsou pode mnou a meli stare id.
     */
    private void updateAcParentForSubnodes(Node node, Node newAcParent, Long oldAcParentId) {
        for (Node subnode : node.getSubnodes()) {
            if (subnode.getAcParent().getId().equals(oldAcParentId)) {
                subnode.setAcParent(newAcParent);
                this.updateAcParentForSubnodes(subnode, newAcParent, oldAcParentId);
            }
        }
    }

    @Override
    public void updateNodeAcpForGroup(Long nodeId, Long groupId, Set<AccessControlPermission> permissions) {
        Node node = nodeService.getNodeById(nodeId);
        UserDetails currentUser = userDetailsService.getCurrentUser();
        this.checkPermission(node, currentUser, AccessControlPermission.EDIT_PERMISSIONS);

        //Get targerGroup
        Group targetGroup = groupService.getGroupById(groupId);
        if (targetGroup == null) {
            throw new GroupNotFoundException("Group with requested ID not found.");
        }

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

        //TODO: change "node.getAcParent().getAcEntries().isEmpty()" to check if ACE for the same target exists
        //TODO: move save recursion into another elseif
        if (accessControlEntry.getAccessControlPermissions().isEmpty() &&
                (node.getAcParent() == null || node.getAcParent().getAcEntries().isEmpty())) {
            for (Node subnode : node.getSubnodes()) {
                this.updateAcParentAcePreserve(subnode, node.getAcParent() == null ? node : node.getAcParent());
            }
            accessControlEntryDao.delete(accessControlEntry);
        } else {
            for (Node subnode : node.getSubnodes()) {
                this.updateAcParentAcePreserve(subnode, node);
            }
            accessControlEntryDao.save(accessControlEntry);
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

    private List<AccessControlEntry> getInheritedAcEntries(Long targetUserId, Node targetNode) {
        if (targetNode.getAcParent() == null) {
            return accessControlEntryDao.getByTargetUserAndNode(targetUserId, targetNode.getId());
        } else {
            return accessControlEntryDao.getByTargetUserAndNode(targetUserId, targetNode.getId(), targetNode.getAcParent().getId());
        }
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
