package cz.cvut.fel.integracniportal.validator;

import cz.cvut.fel.integracniportal.exceptions.PermissionNotFoundException;
import cz.cvut.fel.integracniportal.model.Permission;
import cz.cvut.fel.integracniportal.model.UserRole;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;
import cz.cvut.fel.integracniportal.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Collections;

/**
 * Validator for the {@link cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation}.
 */
@Component
public class UserDetailsResourceValidator implements Validator {

    @Autowired
    UserRoleService userRoleService;

    /**
     * Checks whether the validator is eligible to validate certain class.
     * @param aClass    Class which is being checked.
     * @return True if the class being checked is {@link cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation}, false otherwise.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return UserDetailsRepresentation.class.equals(aClass);
    }

    /**
     * Validates the supplied {@link cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation} for empty fields, checks whether
     * the supplied passwords match and whether the username is not already taken.
     * @param o         Form to be validated. Must be an instance of {@link cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation}
     * @param errors    Spring object, into which all the detected form errors will be added.
     */
    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.empty");
        UserDetailsRepresentation userDetailsRepresentation = (UserDetailsRepresentation) o;

        if (userDetailsRepresentation.getDirectPermissions() != null) {
            for (String permissionName: userDetailsRepresentation.getDirectPermissions()) {
                try {
                    Permission permission = Permission.create(permissionName);
                } catch (PermissionNotFoundException e) {
                    Object[] args = {permissionName};
                    errors.rejectValue("directPermissions", "permission.notFound", args, "Permission does not exist.");
                }
            }
        }
        if (userDetailsRepresentation.getRoles() != null) {
            for (String userRoleName : userDetailsRepresentation.getRoles()) {
                UserRole userRole = userRoleService.getRoleByName(userRoleName);
                if (userRole == null) {
                    Object[] args = {userRoleName};
                    errors.rejectValue("roles", "role.notFound", args, "User role does not exist.");
                }
            }
        }
    }
}
