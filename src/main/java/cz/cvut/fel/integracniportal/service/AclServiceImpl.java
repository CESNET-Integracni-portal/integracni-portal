package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AclPermissionDao;
import cz.cvut.fel.integracniportal.dao.FileMetadataDao;
import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.NodePermission;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eldar Iosip
 */
@Service
@Transactional
public class AclServiceImpl implements AclService {

    private static final Logger logger = Logger.getLogger(AclServiceImpl.class);

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AclPermissionDao aclPermissionDao;

    @Override
    public NodePermission[] getNodePermissionTypes() {
        return NodePermission.values();
    }

    @Override
    public void updateNodePermissions(String fileuuid, Long userId, NodePermission[] permissions) {

        AclPermission aclPermission = aclPermissionDao.getByTargetUserAndFile(userId, fileuuid);
        FileMetadata fileMetadata = fileMetadataService.getFileMetadataByUuid(fileuuid);

        if (aclPermission == null) {
            aclPermission = new AclPermission();

            UserDetails currentUser = userDetailsService.getCurrentUser();
            UserDetails targetUser = userDetailsService.getUserById(userId);

            aclPermission.setTargetFile(fileMetadata);
            aclPermission.setOwner(currentUser);
            aclPermission.setTargetUser(targetUser);
        }


        //TODO: Check if userId can set permissions(EDIT_PERM is allowed)

        aclPermission.getNodePermissions().clear();
        for (NodePermission permission : permissions) {
            logger.info("Adding permission: " + permission.getName());
            aclPermission.getNodePermissions().add(permission);
        }

        aclPermissionDao.save(aclPermission);

        /*
        saveFileMetadata(fileMetadata);
        */
    }
}
