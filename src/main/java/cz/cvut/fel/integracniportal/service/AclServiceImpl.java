package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.NodePermission;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eldar Iosip
 */
@Service
@Transactional
public class AclServiceImpl implements AclService {

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public NodePermission[] getNodePermissionTypes() {
        return NodePermission.values();
    }


    @Override
    public void setPermission(FileMetadataRepresentation targetFile,
                              UserDetailsRepresentation targetUser,
                              NodePermission permission) {
        FileMetadata fileMetadata = fileMetadataService.getFileMetadataByUuid(targetFile.getUuid());

        Map<Long, AclPermission> permissionMap = fileMetadata.getAclPermissions();
    }

    @Override
    public boolean hasPermission(FileMetadataRepresentation targetFile, UserDetailsRepresentation targetUser, NodePermission permission) {
        FileMetadata fileMetadata = fileMetadataService.getFileMetadataByUuid(targetFile.getUuid());

        Map<Long, AclPermission> aclPermissionMap = fileMetadata.getAclPermissions();
        return aclPermissionMap.containsKey(targetUser.getId())
                && aclPermissionMap.get(targetUser.getId()).getNodePermissions().contains(permission);
    }
}
