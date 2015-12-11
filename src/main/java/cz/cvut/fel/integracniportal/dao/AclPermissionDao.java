package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.*;

import java.util.Map;

/**
 * @author Eldar Iosip
 */
public interface AclPermissionDao {

    void save(AclPermission aclPermission);

    AclPermission getByTargetUserAndFile(Long userId, String fileMetadataUuid);

    AclPermission getByTargetUserAndFolder(Long userId, Long folderId);
}