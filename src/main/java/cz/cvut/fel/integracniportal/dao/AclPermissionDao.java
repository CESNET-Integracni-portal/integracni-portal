package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AbstractNode;
import cz.cvut.fel.integracniportal.model.AbstractUser;
import cz.cvut.fel.integracniportal.model.AclPermission;

import java.util.Set;

/**
 * Available operations on user's ACL
 *
 * @author Eldar Iosip
 */
public interface AclPermissionDao {

    Set<AclPermission> getPermissionsForUserNode(AbstractUser user, AbstractNode node);

    void setPermissionsForUserNode(AbstractUser user, AbstractNode node, Set<AclPermission> permissions);

    void save(AclPermission permission);

    void delete(AclPermission permission);
}
