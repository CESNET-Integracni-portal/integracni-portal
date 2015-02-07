package cz.cvut.fel.integracniportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Abstract controller providing validation and other functionality.
 */
@Component
public abstract class AbstractController {

    @Autowired
    protected MessageSource messageSource;

    protected List<String> resolveErrors(BindingResult bindingResult) {
        List<String> resolved = new ArrayList<String>();
        for (MessageSourceResolvable error : bindingResult.getAllErrors()) {
            resolved.add(resolveError(error));
        }
        return resolved;
    }

    protected String resolveError(MessageSourceResolvable error) {
        return messageSource.getMessage(error, Locale.getDefault());
    }
}
