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

import java.util.Set;

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
        Set<AccessControlPermission> accessControlPermissions = aclService.getAccessControlPermissionTypes();

        AccessControlPermissionRepresentation representation = new AccessControlPermissionRepresentation();
        representation.setPermissions(accessControlPermissions);

        return new ResponseEntity<Object>(representation, HttpStatus.OK);
    }

    /**
     * @param nodeId            Node identifier
     * @param userId            Target user identifier
     * @param acpRepresentation Object containing the array of AccessControlPermission instances
     * @return ResponseEntity
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/node/{nodeId}/user/{userId}/update", method = RequestMethod.POST)
    public ResponseEntity updateNodeAclForUser(@PathVariable Long nodeId,
                                               @PathVariable Long userId,
                                               @RequestBody AccessControlPermissionRepresentation acpRepresentation) {
        aclService.updateNodeAceByUser(nodeId, userId, acpRepresentation.getPermissions());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * @param nodeId                                Node identifier
     * @param groupId                               Target group identifier
     * @param accessControlPermissionRepresentation Object containing the array of AccessControlPermission instances
     * @return ResponseEntity
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/node/{nodeId}/group/{groupId}/update", method = RequestMethod.POST)
    public ResponseEntity updateNodeAclForGroup(@PathVariable Long nodeId,
                                                @PathVariable Long groupId,
                                                @RequestBody AccessControlPermissionRepresentation accessControlPermissionRepresentation) {
        aclService.updateNodeAceByGroup(nodeId, groupId, accessControlPermissionRepresentation.getPermissions());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * @param nodeId Node identifier
     * @param userId Target user identifier
     * @return ResponseEntity
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/node/{nodeId}/user/{userId}/permissions", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getNodeAcPermissionsForUser(@PathVariable Long nodeId,
                                                      @PathVariable Long userId) {
        Set<AccessControlPermission> permissions = aclService.getAccessControlPermissions(nodeId, userId);

        AccessControlPermissionRepresentation representation = new AccessControlPermissionRepresentation();
        representation.setPermissions(permissions);

        return new ResponseEntity<Object>(representation, HttpStatus.OK);
    }

}
