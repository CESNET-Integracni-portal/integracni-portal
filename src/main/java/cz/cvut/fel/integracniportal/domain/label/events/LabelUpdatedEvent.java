package cz.cvut.fel.integracniportal.domain.label.events;

import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class LabelUpdatedEvent {

    private final LabelId id;

    private final String name;

    private final String color;

}
