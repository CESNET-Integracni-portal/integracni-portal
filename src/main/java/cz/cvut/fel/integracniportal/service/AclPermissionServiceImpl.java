package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AclPermissionDao;
import cz.cvut.fel.integracniportal.model.AbstractNode;
import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Eldar Iosip
 */
@Service
@Transactional
public class AclPermissionServiceImpl implements AclPermissionService {

    @Autowired
    private AclPermissionDao aclPermissionDao;


    @Override
    public List<AclPermission> getNodeAcl(String nodeId) {
        return null;
    }

    @Override
    public AclPermission getNodeAclForUser(String nodeId, Long userId) {
        return null;
    }

    @Override
    public AclPermission getNodeAclForGroup(String nodeId, Long groupId) {
        return null;
    }

    @Override
    public void setNodeAclForUser(String nodeId, UserDetails user) {

    }

    @Override
    public void setNodeAclForGroup(String nodeId, Long groupId) {

    }

    @Override
    public void setNodeAcl(String nodeId, Long groupId) {

    }

    @Override
    public void createUserPermissions(UserDetails owner, long permissions) {

    }

    @Override
    public void createGroupPermissions(UserDetails group, long permissions) {

    }

    @Override
    public void deletePermission(AclPermission permission, Long permissions) {

    }
}
