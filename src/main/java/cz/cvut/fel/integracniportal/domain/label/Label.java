package cz.cvut.fel.integracniportal.domain.label;

import cz.cvut.fel.integracniportal.domain.label.events.*;
import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.NodeId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@NoArgsConstructor
@Getter
public class Label extends AbstractAnnotatedAggregateRoot<LabelId> {

    @AggregateIdentifier
    private LabelId id;

    private UserId owner;

    private String name;

    private String color;

    private Set<NodeId> labeledNodes = new HashSet<NodeId>();

    public Label(LabelId id, Set<NodeId> labeledNodes) {
        this.id = id;
        this.labeledNodes = labeledNodes;
    }

    public Label(LabelId id, String name, String color, UserId owner) {
        apply(new LabelCreatedEvent(id, name, color, owner));
    }

    public void update(String name, String color) {
        apply(new LabelUpdatedEvent(id, name, color));
    }

    public void delete() {
        apply(new LabelDeletedEvent(id));
    }

    public void addToNode(NodeId nodeId) {
        if (labeledNodes.contains(nodeId)) {
            return;
        }
        apply(new LabelAddedToNodeEvent(id, nodeId));
    }

    public void removeFromNode(NodeId nodeId) {
        if (!labeledNodes.contains(nodeId)) {
            return;
        }
        apply(new LabelRemovedFromNodeEvent(id, nodeId));
    }

    @EventSourcingHandler
    public void onLabelCreated(LabelCreatedEvent event) {
        id = event.getId();
        name = event.getName();
        color = event.getColor();
        owner = event.getOwner();
    }

    @EventSourcingHandler
    public void onLabelDeleted(LabelDeletedEvent event) {
        markDeleted();
    }

    @EventSourcingHandler
    public void labelAdded(LabelAddedToNodeEvent event) {
        labeledNodes.add(event.getNodeId());
    }

    @EventSourcingHandler
    public void labelRemoved(LabelRemovedFromNodeEvent event) {
        labeledNodes.remove(event.getNodeId());
    }

}
