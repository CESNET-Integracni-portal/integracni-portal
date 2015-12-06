package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.NodePermission;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;

/**
 * @author Eldar Iosip
 */
public interface AclService {

    /**
     * Return all the available NodePermission objects.
     *
     * @return NodePermission[]
     */
    public NodePermission[] getNodePermissionTypes();

    /**
     * Add permission for UserDetails on FileMetadata
     */
    public void setPermission(FileMetadataRepresentation targetFile,
                              UserDetailsRepresentation targetUser,
                              NodePermission permission);

    /**
     * Check if targetNode from parameter has requested permission.
     *
     * @param targetFile File to search in
     * @param targetUser User to search for
     * @param permission Permission to check for
     * @return true if targetUser has a permission for targetFile
     */
    public boolean hasPermission(FileMetadataRepresentation targetFile,
                                 UserDetailsRepresentation targetUser,
                                 NodePermission permission);

}
