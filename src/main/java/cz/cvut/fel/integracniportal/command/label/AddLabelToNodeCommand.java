package cz.cvut.fel.integracniportal.command.label;

import cz.cvut.fel.integracniportal.command.node.UserAwareCommand;
import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.NodeId;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class AddLabelToNodeCommand extends UserAwareCommand {

    private final LabelId id;

    private final NodeId nodeId;

}
