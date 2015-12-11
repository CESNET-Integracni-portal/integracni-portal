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
    public AccessControlEntry getByTargetUserAndFile(Long userId, String fileMetadataUuid) {
        return from(accessControlEntry)
                .where(accessControlEntry.targetUser.userId.eq(userId))
                .where(accessControlEntry.targetFile.uuid.eq(fileMetadataUuid))
                .uniqueResult(accessControlEntry);
    }

    @Override
    public AccessControlEntry getByTargetUserAndFolder(Long userId, Long folderId) {
        return from(accessControlEntry)
                .where(accessControlEntry.targetUser.userId.eq(userId))
                .where(accessControlEntry.targetFolder.folderId.eq(folderId))
                .uniqueResult(accessControlEntry);
    }
}
