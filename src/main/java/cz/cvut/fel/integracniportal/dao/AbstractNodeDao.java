package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AbstractNode;
import cz.cvut.fel.integracniportal.model.AbstractUser;
import cz.cvut.fel.integracniportal.model.AclPermission;

import java.util.Set;

/**
 * Node acl list
 *
 * @author Eldar Iosip
 */
public interface AbstractNodeDao {

    AbstractNode getById(String nodeId);

    void update(AbstractNode abstractNode);
}
