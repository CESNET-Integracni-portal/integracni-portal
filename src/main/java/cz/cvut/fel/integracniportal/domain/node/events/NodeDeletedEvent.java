package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.NodeId;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Radek Jezdik
 */
@Getter
@AllArgsConstructor
public abstract class NodeDeletedEvent {

    private NodeId id;

}
