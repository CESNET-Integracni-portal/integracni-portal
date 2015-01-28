package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.exceptions.PermissionNotAssignableToRoleException;
import cz.cvut.fel.integracniportal.model.Permission;
import cz.cvut.fel.integracniportal.model.UserRole;
import cz.cvut.fel.integracniportal.representation.UserRoleRepresentation;
import cz.cvut.fel.integracniportal.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/rest")
public class UserRolesController extends AbstractController {

    @Autowired
    private UserRoleService userRoleService;

    @PreAuthorize("hasRole('main_admin')")
    @RequestMapping(value = "/v0.1/role", method = RequestMethod.GET)
    @ResponseBody
    public List<UserRoleRepresentation> getAllRoles() {

        List<UserRole> userRoles = userRoleService.getAllRoles();
        List<UserRoleRepresentation> userRoleRepresentations = new ArrayList<UserRoleRepresentation>(userRoles.size());
        for (UserRole role : userRoles) {
            userRoleRepresentations.add(new UserRoleRepresentation(role));
        }
        return userRoleRepresentations;
    }

    @PreAuthorize("hasRole('main_admin')")
    @RequestMapping(value = "/v0.1/role", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createRole(@Validated @RequestBody UserRoleRepresentation userRoleRepresentation, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(resolveErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }

        try {

            UserRole userRole = new UserRole();
            userRole.setName(userRoleRepresentation.getName());
            userRole.setDescription(userRoleRepresentation.getDescription());
            userRole.setPermissions(new HashSet<Permission>(userRoleRepresentation.getPermissions()));
            userRoleService.createRole(userRole);
            return new ResponseEntity(new UserRoleRepresentation(userRole), HttpStatus.OK);

        } catch (AlreadyExistsException e) {
            return new ResponseEntity(resolveError(e.getErrorObject()), HttpStatus.CONFLICT);
        } catch (PermissionNotAssignableToRoleException e) {
            return new ResponseEntity(resolveError(e.getErrorObject()), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('main_admin')")
    @RequestMapping(value = "/v0.1/role/{roleid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getRole(@PathVariable("roleid") Long roleId) {
        try {

            UserRole userRole = userRoleService.getRoleById(roleId);
            return new ResponseEntity(new UserRoleRepresentation(userRole), HttpStatus.OK);

        } catch (NotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('main_admin')")
    @RequestMapping(value = "/v0.1/role/{roleid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateRole(@PathVariable("roleid") Long roleId,
                                             @RequestBody UserRoleRepresentation userRoleRepresentation) {

        try {

            UserRole userRole = userRoleService.getRoleById(roleId);
            if (userRoleRepresentation.getName() != null) {
                userRole.setName(userRoleRepresentation.getName());
            }
            if (userRoleRepresentation.getDescription() != null) {
                userRole.setDescription(userRoleRepresentation.getDescription());
            }
            if (userRoleRepresentation.getPermissions() != null) {
                userRole.setPermissions(new HashSet<Permission>(userRoleRepresentation.getPermissions()));
            }
            userRoleService.saveRole(userRole);
            return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

        } catch (NotFoundException e) {
            return new ResponseEntity<String>(resolveError(e.getErrorObject()), HttpStatus.NOT_FOUND);
        } catch (PermissionNotAssignableToRoleException e) {
            return new ResponseEntity<String>(resolveError(e.getErrorObject()), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('main_admin')")
    @RequestMapping(value = "/v0.1/permission", method = RequestMethod.GET)
    @ResponseBody
    public Permission[] getAllPermissions() {
        return Permission.values();
    }

}
