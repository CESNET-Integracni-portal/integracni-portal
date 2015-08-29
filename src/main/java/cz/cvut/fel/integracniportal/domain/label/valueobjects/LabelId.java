package cz.cvut.fel.integracniportal.domain.label.valueobjects;

import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value(staticConstructor = "of")
public class LabelId {

    private final String id;

    public LabelId(String id) {
        this.id = id;
    }

    public static LabelId of(String id) {
        return new LabelId(id);
    }

}
