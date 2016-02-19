package cz.cvut.fel.integracniportal.validator;

import cz.cvut.fel.integracniportal.representation.FileSelectionRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;



@Component
public class FileSelectionValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return FileSelectionRepresentation.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "files", "files.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "folders", "folders.empty");

    }
}
