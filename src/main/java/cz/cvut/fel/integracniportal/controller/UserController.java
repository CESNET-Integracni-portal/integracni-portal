package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;
import cz.cvut.fel.integracniportal.representation.UserPasswordRepresentation;
import cz.cvut.fel.integracniportal.representation.UserPermissionsRepresentation;
import cz.cvut.fel.integracniportal.representation.UserRolesRepresentation;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import cz.cvut.fel.integracniportal.validator.UserDetailsResourceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/rest")
public class UserController extends AbstractController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserDetailsResourceValidator userDetailsResourceValidator;

    @InitBinder()
    public void initBinderUserDetailsResource(WebDataBinder binder) {
        binder.setValidator(userDetailsResourceValidator);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/user", method = RequestMethod.GET)
    @ResponseBody
    public List<UserDetailsRepresentation> getAllUsers() {
        List<UserDetails> userDetailsList = userDetailsService.getAllUsers();
        List<UserDetailsRepresentation> result = new ArrayList<UserDetailsRepresentation>(userDetailsList.size());
        for (UserDetails userDetails : userDetailsList) {
            result.add(new UserDetailsRepresentation(userDetails));
        }
        return result;
    }

    @PreAuthorize("hasAnyRole('externists', 'main_admin')")
    @RequestMapping(value = "/v0.2/user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createUser(@Validated @RequestBody UserDetailsRepresentation userDetailsResource, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(resolveErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = userDetailsService.createUser(userDetailsResource);
        return new ResponseEntity(new UserDetailsRepresentation(userDetails), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('externists', 'main_admin')")
    @RequestMapping(value = "/v0.2/user/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getUser(@PathVariable("userid") String userId) {
        UserDetails userDetails = userDetailsService.getUserById(userId);
        UserDetailsRepresentation userDetailsRepresentation = new UserDetailsRepresentation(userDetails);
        return new ResponseEntity(userDetailsRepresentation, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('externists')")
    @RequestMapping(value = "/v0.2/user/{userid}/passwordChange", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> changePassword(@PathVariable("userid") String userId,
                                                 @RequestBody UserPasswordRepresentation passwordRepresentation) {
        userDetailsService.changePassword(userId, passwordRepresentation.getPassword(), passwordRepresentation.getOldPassword());
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('externists')")
    @RequestMapping(value = "/v0.2/user/{userid}/rolesAssignment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateRoles(@PathVariable("userid") String userId,
                                              @RequestBody UserRolesRepresentation rolesRepresentation) {
        userDetailsService.updateRoles(userId, rolesRepresentation.getRoles());
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('externists')")
    @RequestMapping(value = "/v0.2/user/{userid}/permissionsGrant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updatePermissions(@PathVariable("userid") String userId,
                                                    @RequestBody UserPermissionsRepresentation rolesRepresentation) {
        userDetailsService.updatePermissions(userId, rolesRepresentation.getPermissions());
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/user/current", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getCurrentUser() {
        UserDetails userDetails = userDetailsService.getCurrentUser();
        UserDetailsRepresentation userDetailsRepresentation = new UserDetailsRepresentation(userDetails);
        return new ResponseEntity(userDetailsRepresentation, HttpStatus.OK);
    }

}
