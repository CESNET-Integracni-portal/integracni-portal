package cz.cvut.fel.integracniportal.command.label;

import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class DeleteLabelCommand {

    private final LabelId id;

}
