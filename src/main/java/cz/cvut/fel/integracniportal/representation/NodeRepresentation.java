package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.AbstractNode;
import cz.cvut.fel.integracniportal.model.Group;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eldar Iosip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeRepresentation {

    private String id;

    private String name;

    public NodeRepresentation() {
    }

    public NodeRepresentation(AbstractNode abstractNode) {
        this.id = abstractNode.getId();
        this.name = abstractNode.getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
