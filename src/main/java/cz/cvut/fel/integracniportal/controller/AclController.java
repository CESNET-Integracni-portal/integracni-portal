package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.*;
import cz.cvut.fel.integracniportal.representation.*;
import cz.cvut.fel.integracniportal.service.AclPermissionService;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
     * Returns all available acl rules, for managing by the user.
     *
     * @return List of ENUM values
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/list/permissions", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getNodeAclForUser() {
        NodePermission[] nodePermissions = aclPermissionService.getAllPermissionTypes();

        List<NodePermissionRepresentation> representations = new ArrayList<NodePermissionRepresentation>();
        for (NodePermission nodePermission : nodePermissions) {
            representations.add(new NodePermissionRepresentation(nodePermission));
        }

        return new ResponseEntity<Object>(representations, HttpStatus.OK);
    }

    /**
     * Returns a list of all users and groups and their permissions for selected node.
     *
     * @return ACL representation objects.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/node/{nodeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getNodeAclForCurrentUser(@PathVariable String nodeId) {
        UserDetails currentUser = userService.getCurrentUser();

        Set<NodePermission> nodePermissions = aclPermissionService.getNodeAclForUser(nodeId, currentUser.getId());

        List<NodePermissionRepresentation> representations = new ArrayList<NodePermissionRepresentation>();
        for (NodePermission nodePermission : nodePermissions) {
            representations.add(new NodePermissionRepresentation(nodePermission));
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
        Set<NodePermission> nodePermissions = aclPermissionService.getNodeAclForUser(nodeId, userId);

        List<NodePermissionRepresentation> representations = new ArrayList<NodePermissionRepresentation>();
        for (NodePermission nodePermission : nodePermissions) {
            representations.add(new NodePermissionRepresentation(nodePermission));
        }

        return new ResponseEntity<Object>(representations, HttpStatus.OK);
    }

    /**
     * Update node ACL list for list of users and groups.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/bulk/update/{nodeId}", method = POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity updateNodeAcl(@PathVariable String nodeId,
                                        @RequestBody List<AclPermissionRepresentation> aclPermissionRepresentations) {
        aclPermissionService.updateNodePermissions(nodeId, aclPermissionRepresentations);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
