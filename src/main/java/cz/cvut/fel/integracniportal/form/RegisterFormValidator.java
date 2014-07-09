package cz.cvut.fel.integracniportal.form;

import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for the {@link cz.cvut.fel.integracniportal.form.RegisterForm}.
 */
@Component
public class RegisterFormValidator implements Validator {

    @Autowired
    UserDetailsService userService;

    /**
     * Checks whether the validator is eligible to validate certain class.
     * @param aClass    Class which is being checked.
     * @return True if the class being checked is {@link cz.cvut.fel.integracniportal.form.RegisterForm}, false otherwise.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return RegisterForm.class.equals(aClass);
    }

    /**
     * Validates the supplied {@link cz.cvut.fel.integracniportal.form.RegisterForm} for empty fields, checks whether
     * the supplied passwords match and whether the username is not already taken.
     * @param o         Form to be validated. Must be an instance of {@link cz.cvut.fel.integracniportal.form.RegisterForm}
     * @param errors    Spring object, into which all the detected form errors will be added.
     */
    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.empty");
        RegisterForm registerForm = (RegisterForm) o;
        if (registerForm.getPassword() != null && !registerForm.getPassword().equals(registerForm.getPasswordRepeat())) {
            errors.rejectValue("password", "password.dont-match");
        }
        if (userService.getUserByUsername(registerForm.getUsername()) != null) {
            errors.rejectValue("username", "username.taken");
        }
    }
}
