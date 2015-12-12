package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.*;

/**
 * @author Eldar Iosip
 */
public interface AclPermissionDao {

    void save(AccessControlEntry accessControlEntry);

    AccessControlEntry getByTargetUserAndFile(Long userId, Long fileMetadataId);

    AccessControlEntry getByTargetUserAndFolder(Long userId, Long folderId);
}