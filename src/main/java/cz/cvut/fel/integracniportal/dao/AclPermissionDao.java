package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.NodePermission;
import cz.cvut.fel.integracniportal.representation.AbstractUserRepresentation;
import cz.cvut.fel.integracniportal.representation.NodePermissionRepresentation;

import java.util.List;
import java.util.Set;

/**
 * Available operations on user's ACL
 *
 * @author Eldar Iosip
 */
public interface AclPermissionDao {

    AclPermission getByNodeAndUser(String nodeId, Long userId);

    List<AclPermission> getAllPermissions(String nodeId, Long userId);

    Long setPermissionsByNodeAndUser(
            List<NodePermissionRepresentation> nodePermissions,
            String nodeId,
            AbstractUserRepresentation user
    );

    void save(AclPermission permission);

    void delete(AclPermission permission);
}
