package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AclPermissionDao;
import cz.cvut.fel.integracniportal.model.AbstractNode;
import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Eldar Iosip
 */
@Service
@Transactional
public class AclPermissionServiceImpl implements AclPermissionService {

    @Autowired
    private AclPermissionDao aclPermissionDao;

    @Override
    public AclPermission getUserPermissions(UserDetails owner, AbstractNode node) {
        return null;
    }

    @Override
    public AclPermission createUserPermissions(UserDetails owner, long permissions) {
        return null;
    }

    @Override
    public AclPermission createGroupPermissions(UserDetails group, long permissions) {
        return null;
    }

    @Override
    public AclPermission deletePermission(AclPermission permission, Long permissions) {
        return null;
    }
}
