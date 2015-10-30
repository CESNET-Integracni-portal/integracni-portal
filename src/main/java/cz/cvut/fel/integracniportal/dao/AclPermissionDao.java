package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AbstractNode;
import cz.cvut.fel.integracniportal.model.AbstractUser;
import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.NodePermission;
import cz.cvut.fel.integracniportal.representation.AbstractUserRepresentation;
import cz.cvut.fel.integracniportal.representation.NodePermissionRepresentation;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Available operations on user's ACL
 *
 * @author Eldar Iosip
 */
public interface AclPermissionDao {

    Map<Long, AclPermission> getPermissions(String nodeId, Long userId);

    void update(AclPermission aclPermission);
}