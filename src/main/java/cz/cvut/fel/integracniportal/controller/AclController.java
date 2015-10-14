package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.Group;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.*;
import cz.cvut.fel.integracniportal.service.AclPermissionService;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Eldar Iosip
 */
@Controller
@RequestMapping("/rest")
public class AclController extends AbstractController {

    @Autowired
    private AclPermissionService aclPermissionService;

    @Autowired
    private UserDetailsService userService;

    /**
     * Returns a list of all users and groups and their permissions.
     *
     * @return An ACL representation object.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/node/{nodeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getNodeAcl(@PathVariable String nodeId) {
        List<AclPermission> permissionList = aclPermissionService.getNodeAcl(nodeId);

        List<AclPermissionRepresentation> representations = new ArrayList<>();
        for (AclPermission aclPermission : permissionList) {
            representations.add(new AclPermissionRepresentation(aclPermission));
        }

        return new ResponseEntity<Object>(representations, HttpStatus.OK);
    }

    /**
     * Returns a list of all users and groups and their permissions.
     * <p>
     * May be used in situations, when system need to inform a user about his possibilities
     *
     * @return An ACL representation object.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/node/{nodeId}/user/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getNodeAclForUser(@PathVariable String nodeId, @PathVariable Long userId) {
        AclPermission aclPermission = aclPermissionService.getNodeAclForUser(nodeId, userId);
        return new ResponseEntity<Object>(new AclPermissionRepresentation(aclPermission), HttpStatus.OK);
    }

    /**
     * Returns a list of all permissions, available for selected group.
     * <p>
     * May be used in situations, when system need to inform a group about it's possibilities
     *
     * @return An ACL representation object.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/node/{nodeId}/group/{groupId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getNodeAclForGroup(@PathVariable String nodeId, @PathVariable Long groupId) {
        AclPermission aclPermission = aclPermissionService.getNodeAclForGroup(nodeId, groupId);
        return new ResponseEntity<Object>(new AclPermissionRepresentation(aclPermission), HttpStatus.OK);
    }

    /**
     * Update node ACL list for selected user.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/update/{nodeId}/user/{userId}", method = POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity updateNodeAclForUser(@PathVariable String nodeId, @PathVariable Long userId) {
        return null;
    }

    /**
     * Update node ACL list for selected group.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/update/{nodeId}/group/{groupId}", method = POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity updateNodeAclForGroup(@PathVariable String nodeId, @PathVariable Long groupId) {
        return null;
    }

    /**
     * Update node ACL list for list of users and groups.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/bulk/update/{nodeId}", method = POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity updateNodeAcl(@PathVariable String nodeId) {
        return null;
    }

}
