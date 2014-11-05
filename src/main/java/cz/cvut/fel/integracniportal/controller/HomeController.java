package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.cesnet.CesnetService;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest")
public class HomeController {

    private static final Logger logger = Logger.getLogger(HomeController.class);

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    CesnetService cesnetService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> test() {
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "user/{username}", method = RequestMethod.GET)
    @ResponseBody
    public UserDetailsRepresentation user(@PathVariable("username") String username) {
        UserDetails userDetails = userDetailsService.getUserByUsername(username);
        if (userDetails != null) {
            UserDetailsRepresentation userDetailsResource = new UserDetailsRepresentation(userDetails);
            return userDetailsResource;
        }
        return null;
    }

    @RequestMapping(value = "secure/user/{username}", method = RequestMethod.GET)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public UserDetailsRepresentation secure(@PathVariable("username") String username) {
        UserDetails userDetails = userDetailsService.getUserByUsername(username);
        if (userDetails != null) {
            UserDetailsRepresentation userDetailsResource = new UserDetailsRepresentation(userDetails);
            return userDetailsResource;
        }
        return null;
    }
}
