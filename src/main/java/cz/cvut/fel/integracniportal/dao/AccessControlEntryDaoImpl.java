package cz.cvut.fel.integracniportal.dao;

import com.mysema.query.types.expr.BooleanExpression;
import cz.cvut.fel.integracniportal.model.AccessControlEntry;
import cz.cvut.fel.integracniportal.model.AccessControlPermission;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cz.cvut.fel.integracniportal.model.QAccessControlEntry.accessControlEntry;
import static cz.cvut.fel.integracniportal.model.QGroup.group;

/**
 * @author Eldar Iosip
 */
@Repository
public class AccessControlEntryDaoImpl extends GenericHibernateDao<AccessControlEntry> implements AccessControlEntryDao {

    public AccessControlEntryDaoImpl() {
        super(AccessControlEntry.class);
    }

    //TODO: order by targetUser not null first
    @Override
    public List<AccessControlEntry> getByTargetUserAndNode(Long userId, Long nodeId) {
        return from(accessControlEntry)
                .where(accessControlEntry.targetNode.nodeId.eq(nodeId).and(
                        accessControlEntry.targetUser.userId.eq(userId).or(
                                accessControlEntry.targetGroup.groupId.in(subQuery()
                                        .from(group)
                                        .where(group.members.any().userId.eq(userId))
                                        .list(group.groupId))
                        ))
                )
                .list(accessControlEntry);
    }

    //TODO: order by targetUser not null first
    @Override
    public List<AccessControlEntry> getByTargetUserAndNode(Long userId, Long nodeId, Long aclRootId) {

        return from(accessControlEntry)
                .where(accessControlEntry.targetNode.nodeId.in(nodeId, aclRootId).and(
                        accessControlEntry.targetUser.userId.eq(userId).or(
                                accessControlEntry.targetGroup.groupId.in(subQuery()
                                        .from(group)
                                        .where(group.members.any().userId.eq(userId))
                                        .list(group.groupId))
                        ))
                )
                .list(accessControlEntry);
    }

    @Override
    public AccessControlEntry getByTargetGroupAndNode(Long groupId, Long nodeId) {
        return from(accessControlEntry)
                .where(accessControlEntry.targetNode.nodeId.eq(nodeId).and(
                        accessControlEntry.targetGroup.groupId.eq(groupId)
                ))
                .uniqueResult(accessControlEntry);
    }

    @Override
    public List<AccessControlEntry> getByTargetUserNoOwnerPermission(Long userId, AccessControlPermission permission) {
        return from(accessControlEntry)
                .where(accessControlEntry.owner.userId.ne(userId)
                        .and(accessControlEntry.targetUser.userId.eq(userId).or(
                                accessControlEntry.targetGroup.groupId.in(subQuery()
                                        .from(group)
                                        .where(group.members.any().userId.eq(userId))
                                        .list(group.groupId))
                        ))
                        .and(accessControlEntry.accessControlPermissions.any().eq(permission))
                )
                .list(accessControlEntry);
    }

}