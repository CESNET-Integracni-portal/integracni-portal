package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AclPermission;
import org.springframework.stereotype.Repository;

import static cz.cvut.fel.integracniportal.model.QAclPermission.aclPermission;

/**
 * @author Eldar Iosip
 */
@Repository
public class AclPermissionDaoImpl extends GenericHibernateDao<AclPermission> implements AclPermissionDao {

    public AclPermissionDaoImpl() {
        super(AclPermission.class);
    }

    @Override
    public AclPermission getByTargetUserAndFile(Long userId, String fileMetadataUuid) {
        return from(aclPermission)
                .where(aclPermission.targetUser.userId.eq(userId))
                .where(aclPermission.targetFile.uuid.eq(fileMetadataUuid))
                .uniqueResult(aclPermission);
    }

    @Override
    public AclPermission getByTargetUserAndFolder(Long userId, Long folderId) {
        return from(aclPermission)
                .where(aclPermission.targetUser.userId.eq(userId))
                .where(aclPermission.targetFolder.folderId.eq(folderId))
                .uniqueResult(aclPermission);
    }
}
