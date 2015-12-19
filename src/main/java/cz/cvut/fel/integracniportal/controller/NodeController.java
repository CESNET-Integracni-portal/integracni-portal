package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.representation.*;
import cz.cvut.fel.integracniportal.service.*;
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
public class NodeController extends AbstractController {

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private SpaceService spaceService;

    /**
     * Returns all nodes (file and folders) shared (READ permission) with user (or groups he is member of).
     *
     * @return A node representation objects.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/node/shared", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getFolder(@PathVariable String spaceId) {
        ensureSpace(spaceId);
        SharedNodeRepresentation sharedNodeRepresentation = nodeService.getSharedNodeRepresentation(spaceId);

        return new ResponseEntity(sharedNodeRepresentation, HttpStatus.OK);
    }

    private void ensureSpace(String space) {
        spaceService.getOfType(space); // throws exception if space doesn't exist
    }

}
