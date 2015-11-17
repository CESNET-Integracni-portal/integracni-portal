package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AclPermissionDao;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.*;
import cz.cvut.fel.integracniportal.representation.AclPermissionRepresentation;
import cz.cvut.fel.integracniportal.representation.NodePermissionRepresentation;
import cz.cvut.fel.integracniportal.representation.NodeRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.plugin.dom.exception.InvalidAccessException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eldar Iosip
 */
@Service
@Transactional
public class AclPermissionServiceImpl implements AclPermissionService {

    @Autowired
    private AclPermissionDao aclPermissionDao;

    @Autowired
    private AbstractNodeService abstractNodeService;

    @Override
    @Transactional(readOnly = true)
    public AclPermission getAclPermissionByUser(AbstractNode node, UserDetails user) {
        if (node.getAclPermissions().containsKey(user.getId())) {
            return node.getAclPermissions().get(user.getId());
        } else {
            AclPermission aclPermission = new AclPermission();
            aclPermission.setNode(node);
            aclPermission.setTargetUser(user);
            return aclPermission;
        }
    }

    @Override
    public AclPermission createPermission(NodeRepresentation nodeRepresentation, UserDetails user, List<NodePermissionRepresentation> nodePermissionRepresentations) {
        AbstractNode abstractNode = abstractNodeService.getById(nodeRepresentation.getId());
        AclPermission aclPermission = this.getAclPermissionByUser(abstractNode, user);

        //Hard overwrite permissions
        aclPermission.getNodePermissions().clear();
        for (NodePermissionRepresentation nodePermissionRepresentation : nodePermissionRepresentations) {
            aclPermission.addNodePermission(NodePermission.valueOf(NodePermission.class, nodePermissionRepresentation.getName()));
        }

        abstractNodeService.saveAbstractNode(abstractNode);

        return aclPermission;
    }

    @Override
    public NodePermission[] getAllPermissionTypes() {
        return NodePermission.values();
    }

    @Override
    public Set<NodePermission> getAclPermissions(String nodeId, Long userId) {
        Map<Long, AclPermission> permissionMap = aclPermissionDao.getPermissions(nodeId, userId);
        Set<NodePermission> permissions = new HashSet<>();
        for (AclPermission p : permissionMap.values()) {
            permissions.addAll(p.getNodePermissions());
        }
        return permissions;
    }

    @Override
    public void updateNodePermissions(NodeRepresentation nodeRepresentation, List<AclPermissionRepresentation> aclPermissionRepresentations) {
        AbstractNode node = abstractNodeService.getById(nodeRepresentation.getId());
        Map<Long, AclPermission> aclPermissions = node.getAclPermissions();
        for (AclPermissionRepresentation aclPermissionRepresentation : aclPermissionRepresentations) {
            Long key = aclPermissionRepresentation.getTargetUser().getId();
            if (!aclPermissions.containsKey(key)) {
                AclPermission p = new AclPermission();
                p.setNode(node);
                aclPermissions.put(key, p);
            }

            AclPermission p = aclPermissions.get(key);
            p.setNodePermissions(aclPermissionRepresentation.getNodePermissions());
        }
        abstractNodeService.saveAbstractNode(node);
    }

    @Override
    public boolean hasPermission(NodeRepresentation nodeRepresentation, Long userId, NodePermission permission) {
        boolean hasGroupPermission = false,
                hasUserPermission = false,
                groupInit = false;

        for (AclPermission aclPermission : aclPermissionDao.getPermissions(nodeRepresentation.getId(), userId).values()) {
            boolean contains = aclPermission.getNodePermissions().contains(permission);
            if (aclPermission.getTargetUser() instanceof Group) {
                if (!groupInit) {
                    hasGroupPermission = contains;
                    groupInit = true;
                }
                hasGroupPermission &= contains;
            } else if (aclPermission.getTargetUser() instanceof UserDetails) {
                hasUserPermission = contains;
            }
        }
        return hasGroupPermission | hasUserPermission;
    }

    @Override
    public void setPermission(NodeRepresentation nodeRepresentation, Long userId, NodePermission permission) {
        if (!this.hasPermission(nodeRepresentation, userId, NodePermission.EDIT_PERMISSIONS)) {
            throw new InvalidAccessException("No permission to edit");
        }

        AbstractNode node = abstractNodeService.getById(nodeRepresentation.getId());
        Map<Long, AclPermission> aclPermissions = node.getAclPermissions();

        if (aclPermissions.containsKey(userId)) {
            aclPermissions.get(userId).addNodePermission(permission);
        } else {
            AclPermission aclPermission = new AclPermission();
            aclPermission.addNodePermission(permission);
            aclPermissions.put(userId, new AclPermission());
        }

        abstractNodeService.saveAbstractNode(node);
    }

    @Override
    public void inheritParentAcl(NodeRepresentation nodeRepresentation) {
        AbstractNode abstractNode = abstractNodeService.getById(nodeRepresentation.getId());
        AbstractNode aclParent = abstractNode.getParent().getAclParent();

        abstractNode.setAclParent(aclParent == null ? abstractNode.getParent() : aclParent);
    }

}
