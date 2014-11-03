package cz.cvut.fel.integracniportal.validator;

import cz.cvut.fel.integracniportal.model.UserRole;
import cz.cvut.fel.integracniportal.resource.UserDetailsResource;
import cz.cvut.fel.integracniportal.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for the {@link cz.cvut.fel.integracniportal.resource.UserDetailsResource}.
 */
@Component
public class UserDetailsResourceValidator implements Validator {

    @Autowired
    UserRoleService userRoleService;

    /**
     * Checks whether the validator is eligible to validate certain class.
     * @param aClass    Class which is being checked.
     * @return True if the class being checked is {@link cz.cvut.fel.integracniportal.resource.UserDetailsResource}, false otherwise.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return UserDetailsResource.class.equals(aClass);
    }

    /**
     * Validates the supplied {@link cz.cvut.fel.integracniportal.resource.UserDetailsResource} for empty fields, checks whether
     * the supplied passwords match and whether the username is not already taken.
     * @param o         Form to be validated. Must be an instance of {@link cz.cvut.fel.integracniportal.resource.UserDetailsResource}
     * @param errors    Spring object, into which all the detected form errors will be added.
     */
    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "Uživatelské jméno nesmí být prázdné.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Heslo nesmí být prázdné.");
        UserDetailsResource userDetailsResource = (UserDetailsResource) o;

        for (String userRoleName: userDetailsResource.getUserRoles()) {
            UserRole userRole = userRoleService.getRoleByName(userRoleName);
            if (userRole == null) {
                errors.rejectValue("userRoles", "Uživatelská role " + userRoleName + " neexistuje.");
            }
        }
    }
}
