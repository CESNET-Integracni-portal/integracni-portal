package cz.cvut.fel.integracniportal.command.label;

import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class UpdateLabelCommand {

    private final LabelId id;

    private final String name;

    private final String color;

}
