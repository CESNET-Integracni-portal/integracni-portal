package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.Label;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabelRepresentation {

    private String id;

    private String name;

    private String color;

    public LabelRepresentation() {
    }

    public LabelRepresentation(Label label){
        this.id = label.getId();
        this.name = label.getName();
        this.color = label.getColor();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
