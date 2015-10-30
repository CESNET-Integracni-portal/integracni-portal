package cz.cvut.fel.integracniportal.dao;

import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.JPQLSubQuery;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.query.SimpleSubQuery;
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
    public List<AclPermission> getAllPermissions(String nodeId, Long userId) {
        return from(aclPermission)
                .where(aclPermission.targetUser.id.eq(userId).or(aclPermission.targetUser.id.in(
                        new JPASubQuery().from(group).where(group.members.any().id.eq(userId)).list(group.id)))
                ).list(aclPermission);
    }

    @Override
    public Long setPermissionsByNodeAndUser(List<NodePermissionRepresentation> nodePermissions,
                                            String nodeId,
                                            AbstractUserRepresentation user) {
        AclPermission p = new AclPermission();
        p.setNode();
        BooleanExpression permissionNotExists = new JPASubQuery().from(aclPermission).where(aclPermission.node.id.eq(nodeId).and(aclPermission.targetUser.id.eq(user.getId()))).notExists();
        SimpleSubQuery<String> setModuleName = new JPASubQuery()
                .where(permissionNotExists).unique(Expressions.constant(newName));
        return session.insert(modules).set(modules.name, setModuleName).
                executeWithKey(modules.id);
    }

}
