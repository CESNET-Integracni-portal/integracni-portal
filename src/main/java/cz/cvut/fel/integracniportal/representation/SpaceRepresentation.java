package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Representation class for space.
 *
 * @author Radek Jezdik
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaceRepresentation {

    private String type;

    private String name;

    public SpaceRepresentation(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
