package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.form.LoginForm;
import cz.cvut.fel.integracniportal.form.RegisterForm;
import cz.cvut.fel.integracniportal.form.RegisterFormValidator;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for login and user registration.
 */
@Controller
public class LoginController {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(LoginController.class);

    @Autowired
    UserDetailsService userService;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    private RegisterFormValidator registerFormValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Binds the {@link cz.cvut.fel.integracniportal.form.RegisterFormValidator} validator
     * to the {@link cz.cvut.fel.integracniportal.form.RegisterForm}.
     */
    @InitBinder("registerForm")
    private void initBinder(WebDataBinder binder) {
        binder.addValidators(registerFormValidator);
    }

    /**
     * Shows the login form.
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        return new ModelAndView("login", "loginForm", new LoginForm());
    }

    /**
     * Called by Spring when there were errors during authentication. Shows the login form with error message.
     * @param loginForm    The login form with user credentials.
     * @param result       Object containing a list of errors detected in the form.
     */
    @RequestMapping(value = "/login-error", method = RequestMethod.GET)
    public ModelAndView loginError(HttpServletRequest request,
                                   @ModelAttribute("loginForm")LoginForm loginForm,
                                   BindingResult result) {

        result.rejectValue("username", "login.wrong-credentials");
        return new ModelAndView("login", "loginForm", loginForm);
    }

    /**
     * Called by Spring after the user logged out. Shows the login form for new login.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout() {
        return login();
    }

    /**
     * Shows the register form for registering a new user.
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register() {
        return new ModelAndView("register", "registerForm", new RegisterForm());
    }

    /**
     * Processes the registration form. If there were errors, the form will be displayed with them listed.
     * If the form is valid, a new {@link cz.cvut.fel.integracniportal.model.UserDetails} credentials will be created
     * and the user will be automatically logged in.
     * @param registerForm    The register form to be processed.
     * @param result          Object containing a list of errors detected in the form.
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView registerProcess(HttpServletRequest request,
                                        @Validated @ModelAttribute("registerForm")RegisterForm registerForm,
                                        BindingResult result) {

        logger.info("Registering new user: " + registerForm.getUsername());
        UserDetails user = new UserDetails();
        user.setUsername(registerForm.getUsername());
        // Encode the password
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        if (!result.hasErrors()) {
            try {
                userService.saveUser(user);
            } catch (Exception e) {
                logger.error("Unable to register new user - username " + registerForm.getUsername() + " is already taken.");
                result.rejectValue("username", "username.taken");
            }
        }

        if (result.hasErrors()) {
            return new ModelAndView("register", "registerForm", registerForm);
        }

        logger.info("User " + registerForm.getUsername() + " created.");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                registerForm.getUsername(), registerForm.getPassword());
        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authenticatedUser = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
        return new ModelAndView("redirect:/");
    }
}
