package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.OrganizationalUnit;
import cz.cvut.fel.integracniportal.representation.OrganizationalUnitRepresentation;
import cz.cvut.fel.integracniportal.service.OrganizationalUnitService;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/rest")
public class OrganizationalUnitsController extends AbstractController {

    private static final Logger logger = Logger.getLogger(OrganizationalUnitsController.class);

    @Autowired
    private OrganizationalUnitService organizationalUnitService;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Get all organizational units.
     */
    @RequestMapping(value = "/v0.1/unit", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getAllUnits() {
        List<OrganizationalUnit> units = organizationalUnitService.getAllOrganizationalUnits();
        List<OrganizationalUnitRepresentation> representations = new ArrayList<OrganizationalUnitRepresentation>();
        for (OrganizationalUnit unit: units) {
            representations.add(new OrganizationalUnitRepresentation(unit));
        }
        return new ResponseEntity<Object>(representations, HttpStatus.OK);
    }

    /**
     * Get a specific organizational unit.
     * @param id    Id of the organizational unit.
     * @return Organizational unit.
     */
    @RequestMapping(value = "/v0.1/unit/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getUnit(@PathVariable("id") Long id) {
        OrganizationalUnit unit = organizationalUnitService.getOrganizationalUnitById(id);
        if (unit == null) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
        OrganizationalUnitRepresentation representation = new OrganizationalUnitRepresentation(unit);
        return new ResponseEntity<Object>(representation, HttpStatus.OK);
    }

    /**
     * Update a organizational unit.
     * @param id    Id of the organizational unit.
     */
    @RequestMapping(value = "/v0.1/unit/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> alfrescoUpdateFileMetadata(@PathVariable("id") Long id,
                                                             @RequestBody OrganizationalUnitRepresentation organizationalUnitRepresentation) {
        OrganizationalUnit unit = organizationalUnitService.getOrganizationalUnitById(id);
        if (unit == null) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }

        // Currently, the only thing that can be updated is list of admins
        if (organizationalUnitRepresentation.getAdmins() != null) {
            organizationalUnitService.setAdmins(unit, organizationalUnitRepresentation.getAdmins());
        }

        return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
    }

}