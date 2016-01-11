package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.PolicyType;
import cz.cvut.fel.integracniportal.representation.PolicyTypeRepresentation;
import cz.cvut.fel.integracniportal.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Set;

/**
 * @author Eldar Iosip
 */
@Controller
@RequestMapping("/rest")
public class PolicyController extends AbstractController {

    @Autowired
    private PolicyService policyService;

    /**
     * Return all the available policy types.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/policy/type", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getPolicyTypes() {
        Set<PolicyType> types = policyService.getPolicyTypes();

        PolicyTypeRepresentation representation = new PolicyTypeRepresentation();
        representation.setTypes(types);

        return new ResponseEntity<PolicyTypeRepresentation>(representation, HttpStatus.OK);
    }

}
