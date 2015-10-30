package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AclPermissionDao;
import cz.cvut.fel.integracniportal.model.*;
import cz.cvut.fel.integracniportal.representation.AbstractUserRepresentation;
import cz.cvut.fel.integracniportal.representation.AclPermissionRepresentation;
import cz.cvut.fel.integracniportal.representation.NodePermissionRepresentation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.acl.Acl;
import java.util.List;
import java.util.Map;

/**
 * @author Eldar Iosip
 */
@Service
@Transactional
public class AclPermissionServiceImpl implements AclPermissionService {


    private static final Logger logger = Logger.getLogger(AclPermissionServiceImpl.class);

    @Autowired
    private AclPermissionDao aclPermissionDao;

    @Override
    public NodePermission[] getAllPermissionTypes() {
        return NodePermission.values();
    }

    @Override
    public List<NodePermission> getNodeAclForUser(String nodeId, Long userId) {
        return aclPermissionDao.getByNodeAndUser(nodeId, userId).getNodePermissions();
    }

    @Override
    public void updateAclNodePermissions(String nodeId, List<AclPermissionRepresentation> aclPermissionRepresentations) {
        for (AclPermissionRepresentation aclPermissionRepresentation : aclPermissionRepresentations) {
            aclPermissionDao.setPermissionsByNodeAndUser(
                    aclPermissionRepresentation.getNodePermissions(),
                    nodeId,
                    aclPermissionRepresentation.getTargetUser()
            );
        }
    }

    @Override
    public boolean hasPermission(String nodeId, Long userId, NodePermission permission) {
        boolean hasGroupPermission = false,
                hasUserPermission = false;

        for (AclPermission aclPermission : aclPermissionDao.getAllPermissions(nodeId, userId)) {
            boolean contains = aclPermission.getNodePermissions().contains(permission);
            if (aclPermission.getTargetUser() instanceof Group) {
                hasGroupPermission &= contains;
            } else if (aclPermission.getTargetUser() instanceof UserDetails) {
                hasUserPermission = contains;
            }
        }
        return hasGroupPermission | hasUserPermission;
    }

    @Override
    public void setPermission(String nodeId, Long userId, NodePermission permission) {
    }

}
