package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AbstractNodeDao;
import cz.cvut.fel.integracniportal.dao.AclPermissionDao;
import cz.cvut.fel.integracniportal.model.*;
import cz.cvut.fel.integracniportal.representation.AbstractUserRepresentation;
import cz.cvut.fel.integracniportal.representation.AclPermissionRepresentation;
import cz.cvut.fel.integracniportal.representation.NodePermissionRepresentation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.soap.Node;
import java.security.acl.Acl;
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
    private AbstractNodeDao abstractNodeDao;

    @Autowired
    private AclPermissionDao aclPermissionDao;

    @Override
    public NodePermission[] getAllPermissionTypes() {
        return NodePermission.values();
    }

    @Override
    public Set<NodePermission> getNodeAclForUser(String nodeId, Long userId) {
        Map<Long, AclPermission> permissionMap = aclPermissionDao.getPermissions(nodeId, userId);
        Set<NodePermission> permissions = new HashSet<NodePermission>();
        for (AclPermission p : permissionMap.values()) {
            permissions.addAll(p.getNodePermissions());
        }
        return permissions;
    }

    @Override
    public void updateNodePermissions(String nodeId, List<AclPermissionRepresentation> aclPermissionRepresentations) {
        AbstractNode node = abstractNodeDao.getById(nodeId);
        Map<Long, AclPermission> aclPermissions = node.getAcl();
        for (AclPermissionRepresentation aclPermissionRepresentation : aclPermissionRepresentations) {
            Long key = aclPermissionRepresentation.getTargetUser().getId();
            if (!aclPermissions.containsKey(key)) {
                AclPermission p = new AclPermission();
                p.setNode(node);
                aclPermissions.put(key, p);
            }

            AclPermission p = aclPermissions.get(key);
            p.setNodePermissions(aclPermissionRepresentation.getNodePermissions());

            aclPermissionDao.update(p);
        }
    }

    @Override
    public boolean hasPermission(String nodeId, Long userId, NodePermission permission) {
        //TODO: rewrite - could not work like that!
        /*
        boolean hasGroupPermission = false,
                hasUserPermission = false;

        for (AclPermission aclPermission : aclPermissionDao.getPermissions(nodeId, userId)) {
            boolean contains = aclPermission.getNodePermissions().contains(permission);
            if (aclPermission.getTargetUser() instanceof Group) {
                hasGroupPermission &= contains;
            } else if (aclPermission.getTargetUser() instanceof UserDetails) {
                hasUserPermission = contains;
            }
        }
        return hasGroupPermission | hasUserPermission;
        */
        return false;
    }

    @Override
    public void setPermission(String nodeId, Long userId, NodePermission permission) {
    }

    @Override
    public void inheritParentAcl(String nodeId) {
        AbstractNode abstractNode = abstractNodeDao.getById(nodeId);
        AbstractNode aclParent = abstractNode.getParent().getAclParent();

        abstractNode.setAclParent(aclParent == null ? abstractNode.getParent() : aclParent);
    }

}
