package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.resource.UserDetailsResource;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/rest")
public class HomeController {

    @Autowired
    UserDetailsService userDetailsService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> test() {
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "user/{username}", method = RequestMethod.GET)
    @ResponseBody
    public UserDetailsResource home(@PathVariable("username") String username) {
        UserDetails userDetails = userDetailsService.getUserByUsername(username);
        if (userDetails != null) {
            UserDetailsResource userDetailsResource = new UserDetailsResource(userDetails);
            return userDetailsResource;
        }
        return null;
    }
}
