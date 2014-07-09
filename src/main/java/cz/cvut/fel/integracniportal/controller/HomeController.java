package cz.cvut.fel.integracniportal.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HomeController {

    @RequestMapping(method = RequestMethod.GET)
    public String home() {
        return "home";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/auth-only", method = RequestMethod.GET)
    public String authOnlyPage() {
        return "auth-only";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/admin-only", method = RequestMethod.GET)
    public String adminOnlyPage() {
        return "admin-only";
    }
}
