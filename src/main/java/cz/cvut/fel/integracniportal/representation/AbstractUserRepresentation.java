package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.*;

/**
 * Representation class for abstract user.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbstractUserRepresentation {

    private Long id;

    public AbstractUserRepresentation() {
    }

    public AbstractUserRepresentation(AbstractUser abstractUser) {
        this.id = abstractUser.getId();
    }

    public Long getId() {
        return id;
    }
}
