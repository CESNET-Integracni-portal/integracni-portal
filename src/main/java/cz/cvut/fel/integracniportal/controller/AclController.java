package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.NodePermission;
import cz.cvut.fel.integracniportal.representation.NodePermissionRepresentation;
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
     * TODO: add doc
     */
    //@PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/acl/permission", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getPermissions() {
        NodePermission[] nodePermissions = aclService.getNodePermissionTypes();

        NodePermissionRepresentation representation = new NodePermissionRepresentation();
        representation.setPermissions(nodePermissions);

        return new ResponseEntity<Object>(representation, HttpStatus.OK);
    }

}
