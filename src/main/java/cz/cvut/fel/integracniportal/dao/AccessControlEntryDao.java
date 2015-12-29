package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AccessControlEntry;
import cz.cvut.fel.integracniportal.model.AccessControlPermission;

import java.util.List;

/**
 * @author Eldar Iosip
 */
public interface AccessControlEntryDao {

    void save(AccessControlEntry accessControlEntry);

    void delete(AccessControlEntry accessControlEntry);

    List<AccessControlEntry> getByTargetUserGroupsAndNode(Long userId, Long nodeId);

    List<AccessControlEntry> getByTargetUserGroupsAndNode(Long userId, Long nodeId,  Long aclRootId);

    AccessControlEntry getByTargetUserAndNode(Long userId, Long nodeId);

    AccessControlEntry getByTargetGroupAndNode(Long groupId, Long nodeId);

    List<AccessControlEntry> getByTargetUserNoOwnerPermission(Long userId, AccessControlPermission permission);
}