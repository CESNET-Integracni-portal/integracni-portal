package cz.cvut.fel.integracniportal.dao;

import com.mysema.query.jpa.JPASubQuery;
import cz.cvut.fel.integracniportal.model.*;

import cz.cvut.fel.integracniportal.representation.NodePermissionRepresentation;
import org.springframework.stereotype.Repository;

import static cz.cvut.fel.integracniportal.model.QAclPermission.aclPermission;
import static cz.cvut.fel.integracniportal.model.QGroup.group;
import static cz.cvut.fel.integracniportal.model.QAbstractNode.abstractNode;

import java.util.List;
import java.util.Map;

/**
 * @author Eldar Iosip
 */
@Repository
public class AclPermissionDaoImpl extends GenericHibernateDao<AclPermission> implements AclPermissionDao {

    public AclPermissionDaoImpl() {
        super(AclPermission.class);
    }

    @Override
    public Map<Long, AclPermission> getPermissions(String nodeId, Long userId) {
        QAbstractNode aclParent = new QAbstractNode("aclParent");
        QAclPermission aclParentPermission = new QAclPermission("aclPermission");

        AbstractNode node = from(abstractNode)
                .leftJoin(abstractNode.acl, aclPermission)
                .on(aclPermission.node.id.eq(nodeId).and(
                        aclPermission.targetUser.id.eq(userId).or(aclPermission.targetUser.id.in(
                                        new JPASubQuery()
                                                .from(group)
                                                .where(group.members.any().id.eq(userId))
                                                .list(group.id))
                        )
                ))
                .leftJoin(abstractNode.aclParent, aclParent)
                .leftJoin(aclParent.acl, aclParentPermission)
                .on(aclParentPermission.node.id.eq(nodeId).and(
                        aclParentPermission.targetUser.id.eq(userId).or(aclParentPermission.targetUser.id.in(
                                        new JPASubQuery()
                                                .from(group)
                                                .where(group.members.any().id.eq(userId))
                                                .list(group.id))
                        )
                ))
                .where(abstractNode.id.eq(nodeId))
                .singleResult(abstractNode);

        if (node == null) {
            throw new NullPointerException("Node does not exist.");
        }

        return node.getAclPermissions();
    }
}
