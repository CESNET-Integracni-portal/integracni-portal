package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AclPermissionDao;
import cz.cvut.fel.integracniportal.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private FolderService folderService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AclPermissionDao aclPermissionDao;

    @Override
    public AccessControlPermission[] getNodePermissionTypes() {
        return AccessControlPermission.values();
    }

    @Override
    public void updateNodePermissions(Long fileId, Long userId, AccessControlPermission[] permissions) {
       /* AccessControlEntry accessControlEntry = aclPermissionDao.getByTargetUserAndFile(userId, fileId);
        FileMetadata fileMetadata = fileMetadataService.getFileMetadataById(fileId);

        if (accessControlEntry == null) {
            accessControlEntry = new AccessControlEntry();

            UserDetails currentUser = userDetailsService.getCurrentUser();
            UserDetails targetUser = userDetailsService.getUserById(userId);

            accessControlEntry.setTargetFile(fileMetadata);
            accessControlEntry.setOwner(currentUser);
            accessControlEntry.setTargetUser(targetUser);
        }


        //TODO: Check if userId can set permissions(EDIT_PERM is allowed)

        accessControlEntry.getAccessControlPermissions().clear();
        for (AccessControlPermission permission : permissions) {
            logger.info("Adding permission: " + permission.getName());
            accessControlEntry.getAccessControlPermissions().add(permission);
        }

        aclPermissionDao.save(accessControlEntry);
        */
    }

    @Override
    public void updateFolderNodePermissions(Long folderId, Long userId, AccessControlPermission[] permissions) {
        /*AccessControlEntry accessControlEntry = aclPermissionDao.getByTargetUserAndFolder(userId, folderId);
        Folder folder = folderService.getFolderById(folderId);

        if (accessControlEntry == null) {
            accessControlEntry = new AccessControlEntry();

            UserDetails currentUser = userDetailsService.getCurrentUser();
            UserDetails targetUser = userDetailsService.getUserById(userId);

            accessControlEntry.setTargetFolder(folder);
            accessControlEntry.setOwner(currentUser);
            accessControlEntry.setTargetUser(targetUser);
        }


        //TODO: Check if userId can set permissions(EDIT_PERM is allowed)

        accessControlEntry.getAccessControlPermissions().clear();
        for (AccessControlPermission permission : permissions) {
            logger.info("Adding permission: " + permission.getName());
            accessControlEntry.getAccessControlPermissions().add(permission);
        }

        aclPermissionDao.save(accessControlEntry);
        */
    }

}
