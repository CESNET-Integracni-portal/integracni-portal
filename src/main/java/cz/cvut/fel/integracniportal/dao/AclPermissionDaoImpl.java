package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.*;

import cz.cvut.fel.integracniportal.representation.AbstractUserRepresentation;
import cz.cvut.fel.integracniportal.representation.NodePermissionRepresentation;
import org.springframework.stereotype.Repository;

import static cz.cvut.fel.integracniportal.model.QAclPermission.aclPermission;
import static cz.cvut.fel.integracniportal.model.QGroup.group;

import java.util.List;

/**
 * @author Eldar Iosip
 */
@Repository
public class AclPermissionDaoImpl extends GenericHibernateDao<AclPermission> implements AclPermissionDao {

    public AclPermissionDaoImpl() {
        super(AclPermission.class);
    }

    @Override
    public AclPermission getByNodeAndUser(String nodeId, Long userId) {
        return from(aclPermission)
                .where(aclPermission.targetUser.id.eq(userId))
                .singleResult(aclPermission);
    }

    @Override
    public AclPermission hasPermissionTo(String nodeId, Long userId, NodePermission nodePermission) {
        //Group[] groups = from(group).where(group.members.contains());
        return null;
    }

    @Override
    public void setPermissionsByNodeAndUser(List<NodePermissionRepresentation> nodePermissions, String nodeId, AbstractUserRepresentation user) {

    }

}
