package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AccessControlEntry;
import org.springframework.stereotype.Repository;

import static cz.cvut.fel.integracniportal.model.QAccessControlEntry.accessControlEntry;

/**
 * @author Eldar Iosip
 */
@Repository
public class AclPermissionDaoImpl extends GenericHibernateDao<AccessControlEntry> implements AclPermissionDao {

    public AclPermissionDaoImpl() {
        super(AccessControlEntry.class);
    }

    @Override
    public AccessControlEntry getByTargetUserAndFile(Long userId, Long fileMetadataId) {
        /*
        return from(accessControlEntry)
                .where(accessControlEntry.targetUser.userId.eq(userId))
                .where(accessControlEntry.targetFile.nodeId.eq(fileMetadataId))
                .uniqueResult(accessControlEntry);
                */
        return null;
    }

    @Override
    public AccessControlEntry getByTargetUserAndFolder(Long userId, Long folderId) {
        return from(accessControlEntry)
                .where(accessControlEntry.targetUser.userId.eq(userId))
                .where(accessControlEntry.targetFolder.nodeId.eq(folderId))
                .uniqueResult(accessControlEntry);
    }
}
