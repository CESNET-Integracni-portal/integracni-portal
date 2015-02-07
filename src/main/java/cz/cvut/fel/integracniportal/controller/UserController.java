package cz.cvut.fel.integracniportal.controller;

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

    @PreAuthorize("hasAnyRole('externists', 'main_admin')")
    @RequestMapping(value = "/v0.1/user", method = RequestMethod.GET)
    @ResponseBody
    public List<UserDetailsRepresentation> getAllUsers() {
        List<UserDetails> userDetailsList = userDetailsService.getAllUsers();
        List<UserDetailsRepresentation> result = new ArrayList<UserDetailsRepresentation>(userDetailsList.size());
        for (UserDetails userDetails : userDetailsList) {
            result.add(new UserDetailsRepresentation(userDetails));
        }
        return result;
    }

    @PreAuthorize("hasRole('externists')")
    @RequestMapping(value = "/v0.1/user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createUser(@Validated @RequestBody UserDetailsRepresentation userDetailsResource, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(resolveErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = userDetailsService.createUser(userDetailsResource);
        return new ResponseEntity(new UserDetailsRepresentation(userDetails), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('externists', 'main_admin')")
    @RequestMapping(value = "/v0.1/user/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getUser(@PathVariable("userid") Long userId) {
        UserDetails userDetails = userDetailsService.getUserById(userId);
        UserDetailsRepresentation userDetailsRepresentation = new UserDetailsRepresentation(userDetails);
        return new ResponseEntity(userDetailsRepresentation, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('externists')")
    @RequestMapping(value = "/v0.1/user/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateUser(@PathVariable("userid") Long userId,
                                             @RequestBody UserDetailsRepresentation userDetailsResource) {
        userDetailsService.updateUser(userId, userDetailsResource);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/user/current", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getCurrentUser() {
        UserDetails userDetails = userDetailsService.getCurrentUser();
        UserDetailsRepresentation userDetailsRepresentation = new UserDetailsRepresentation(userDetails);
        return new ResponseEntity(userDetailsRepresentation, HttpStatus.OK);
    }

}
