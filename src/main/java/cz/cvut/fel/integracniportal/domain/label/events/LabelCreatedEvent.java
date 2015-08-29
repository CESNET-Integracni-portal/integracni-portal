package cz.cvut.fel.integracniportal.domain.label.events;

import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class LabelCreatedEvent {

    private final LabelId id;

    private final String name;

    private final String color;

    private final UserId owner;

}
