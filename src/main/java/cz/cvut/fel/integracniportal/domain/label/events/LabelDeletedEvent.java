package cz.cvut.fel.integracniportal.domain.label.events;

import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class LabelDeletedEvent {

    private final LabelId id;

}
