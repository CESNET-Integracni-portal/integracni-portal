package cz.cvut.fel.integracniportal.domain.label.events;

import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.NodeId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class LabelRemovedFromNodeEvent {

    private final LabelId id;

    private final NodeId nodeId;

}
