package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.Label;

/**
 * Created by Vavat on 7. 3. 2015.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabelRepresentation {

    private Long id;

    private String name;

    private String color;

    private Long owner;

    public LabelRepresentation() {
    }

    public LabelRepresentation(Label label){
        this.id = label.getId();
        this.name = label.getName();
        this.color = label.getColor();
        this.owner = label.getOwner().getId();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }
}
