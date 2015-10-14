package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.*;

/**
 * Representation class for node permission.
 *
 * @author Eldar Iosip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodePermissionRepresentation {

    private String name;

    public NodePermissionRepresentation() {
    }

    public NodePermissionRepresentation(NodePermission nodePermission) {
        this.name = nodePermission.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
