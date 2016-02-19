package cz.cvut.fel.integracniportal.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class AggregateValidator implements Validator{

    private Validator[] validators;

    public AggregateValidator(final Validator[] validators){
        super();
        this.validators = validators;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        for(Validator v: this.validators){
            if(v.supports(clazz)){
                return(true);
            }
        }
        return(false);
    }

    @Override
    public void validate(Object target, Errors errors) {
        for(Validator v: this.validators){
            if(v.supports(target.getClass())){
                v.validate(target,errors);
                break;
            }
        }
    }
}
