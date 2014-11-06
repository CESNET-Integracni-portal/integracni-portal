package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.exceptions.UserRoleNotFoundException;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;
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

    @RequestMapping(value = "/v0.1/users", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public List<UserDetailsRepresentation> getAllUsers() {
        List<UserDetails> userDetailsList = userDetailsService.getAllUsers();
        List<UserDetailsRepresentation> result = new ArrayList<UserDetailsRepresentation>(userDetailsList.size());
        for (UserDetails userDetails: userDetailsList) {
            result.add(userDetailsToResource(userDetails));
        }
        return result;
    }

    @RequestMapping(value = "/v0.1/users", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> createUser(@Validated @RequestBody UserDetailsRepresentation userDetailsResource, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<Object>(resolveErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }

        try {
            UserDetails userDetails = userDetailsService.createUser(userDetailsResource);
            return new ResponseEntity<Object>("/rest/v0.1/user/"+userDetails.getUserId(), HttpStatus.OK);
        } catch (AlreadyExistsException e) {
            return new ResponseEntity<Object>(resolveError(e.getErrorObject()), HttpStatus.CONFLICT);
        } catch (UserRoleNotFoundException e) {
            return new ResponseEntity<Object>(resolveError(e.getErrorObject()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/v0.1/user/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public UserDetailsRepresentation getUser(@PathVariable("userid") Long userId) {
        UserDetails userDetails = userDetailsService.getUserById(userId);
        return userDetailsToResource(userDetails);
    }

    private UserDetailsRepresentation userDetailsToResource(UserDetails userDetails) {
        UserDetailsRepresentation userDetailsResource = new UserDetailsRepresentation();
        userDetailsResource.setUserId(userDetails.getUserId());
        userDetailsResource.setUsername(userDetails.getUsername());
        return userDetailsResource;
    }

    @RequestMapping(value = "/v0.1/user/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateUser(@PathVariable("userid") Long userId,
                                             @RequestBody UserDetailsRepresentation userDetailsResource) {

        try {
            UserDetails userDetails = userDetailsService.updateUser(userId, userDetailsResource);
            return new ResponseEntity<String>("/rest/v0.1/user/"+userDetails.getUserId(), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<String>(resolveError(e.getErrorObject()), HttpStatus.NOT_FOUND);
        } catch (UserRoleNotFoundException e) {
            return new ResponseEntity<String>(resolveError(e.getErrorObject()), HttpStatus.BAD_REQUEST);
        }
    }

}
