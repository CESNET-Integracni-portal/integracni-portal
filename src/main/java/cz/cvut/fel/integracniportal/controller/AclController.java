package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.AccessControlPermission;
import cz.cvut.fel.integracniportal.representation.AccessControlPermissionRepresentation;
import cz.cvut.fel.integracniportal.service.AclService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Eldar Iosip
 */
@Controller
@RequestMapping("/rest")
public class AclController extends AbstractController {

    @Autowired
    private AclService aclService;

    /**
     * Return all the available AccessControlPermission instances.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/permission", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getPermissions() {
        AccessControlPermission[] accessControlPermissions = aclService.getNodePermissionTypes();

        AccessControlPermissionRepresentation representation = new AccessControlPermissionRepresentation();
        representation.setPermissions(accessControlPermissions);

        return new ResponseEntity<Object>(representation, HttpStatus.OK);
    }

    /**
     * Update ACL for selected user in space with node.
     *
     * @param fileId                                File identifier
     * @param userId                                Target user identifier
     * @param accessControlPermissionRepresentation Object containing the array of AccessControlPermission instances
     * @return ResponseEntity
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/file/{fileId}/user/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity updateUserAclForFile(@PathVariable Long fileId,
                                               @PathVariable Long userId,
                                               @RequestBody AccessControlPermissionRepresentation accessControlPermissionRepresentation) {
        aclService.updateNodePermissions(fileId, userId, accessControlPermissionRepresentation.getPermissions());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Update ACL for selected user in space with node.
     *
     * @param folderId                              Folder identifier
     * @param userId                                Target user identifier
     * @param accessControlPermissionRepresentation Object containing the array of AccessControlPermission instances
     * @return ResponseEntity
     */
    /*
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/folder/{folderId}/user/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity updateUserAclForFolder(@PathVariable Long folderId,
                                                 @PathVariable Long userId,
                                                 @RequestBody AccessControlPermissionRepresentation accessControlPermissionRepresentation) {
        aclService.updateFolderNodePermissions(folderId, userId, accessControlPermissionRepresentation.getPermissions());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    */

}
