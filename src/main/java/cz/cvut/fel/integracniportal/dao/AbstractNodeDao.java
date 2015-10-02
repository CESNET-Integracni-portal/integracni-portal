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
public interface AbstractNodeDao {

    AbstractNode getAclParentFor(String nodeId);

    void setAclParentFor(String nodeId);

    void save(AbstractNode abstractNode);

    void delete(AbstractNode abstractNode);
}
