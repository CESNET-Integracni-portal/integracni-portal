package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.*;

import java.util.Map;

/**
 * @author Eldar Iosip
 */
public interface AclPermissionDao {

    Map<Long, AclPermission> getPermissions(String nodeId, Long userId);

    void update(AclPermission aclPermission);

    void createAclPermission(AclPermission aclPermission);
}