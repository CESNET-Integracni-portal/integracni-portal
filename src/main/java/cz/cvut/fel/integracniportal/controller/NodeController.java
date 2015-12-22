package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.Policy;
import cz.cvut.fel.integracniportal.model.UserDetails;
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
    private NodeService nodeService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private PolicyService policyService;

    /**
     * Returns all nodes (file and folders) shared (READ permission) with user (or groups he is member of).
     *
     * @return A node representation objects.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/node/shared", method = RequestMethod.GET)
    public ResponseEntity getFolder(@PathVariable String spaceId) {
        ensureSpace(spaceId);
        SharedNodeRepresentation sharedNodeRepresentation = nodeService.getSharedNodeRepresentation(spaceId);

        return new ResponseEntity<SharedNodeRepresentation>(sharedNodeRepresentation, HttpStatus.OK);
    }

    /**
     * Create new policy rule for selected node.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/node/{nodeId}/policy", method = RequestMethod.POST)
    public ResponseEntity createPolicy(@PathVariable String spaceId,
                                       @PathVariable Long nodeId,
                                       @RequestBody PolicyRepresentation policyRepresentation) {
        ensureSpace(spaceId);

        Policy createdPolicy = policyService.createPolicy(
                nodeId,
                policyRepresentation.getType(),
                policyRepresentation.getActiveAfter()
        );

        return new ResponseEntity<PolicyRepresentation>(new PolicyRepresentation(createdPolicy), HttpStatus.CREATED);
    }

    /**
     * Update existing policy rule for selected node.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/node/{nodeId}/policy", method = RequestMethod.PUT)
    public ResponseEntity updatePolicy(@PathVariable String spaceId,
                                       @PathVariable Long nodeId,
                                       @RequestBody PolicyRepresentation policyRepresentation) {
        ensureSpace(spaceId);

        Policy updatedPolicy = policyService.updatePolicy(
                policyRepresentation.getId(),
                policyRepresentation.getType(),
                policyRepresentation.getActiveAfter()
        );

        return new ResponseEntity<PolicyRepresentation>(new PolicyRepresentation(updatedPolicy), HttpStatus.OK);
    }

    /**
     * Delete existing policy rule for selected node.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/node/{nodeId}/policy/{policyId}", method = RequestMethod.DELETE)
    public ResponseEntity deletePolicy(@PathVariable String spaceId,
                                       @PathVariable Long nodeId,
                                       @PathVariable Long policyId) {
        ensureSpace(spaceId);

        policyService.deletePolicy(policyId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    private void ensureSpace(String space) throws NotFoundException {
        spaceService.getOfType(space);
    }

}
