package cz.cvut.fel.integracniportal.validator;


import cz.cvut.fel.integracniportal.representation.NewPasswordRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component
public class PasswordChangeValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return NewPasswordRepresentation.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oldPassword", "username.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password2", "password2.empty");

        NewPasswordRepresentation newPass = (NewPasswordRepresentation) target;

        if(!newPass.getPassword().equals(newPass.getPassword2())){
            errors.reject("password2", "passwords do not match");
        }
    }
}
