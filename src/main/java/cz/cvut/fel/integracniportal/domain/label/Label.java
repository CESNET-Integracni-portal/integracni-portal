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

    public Label(LabelId id, String name, String color, UserId owner) {
        apply(new LabelCreatedEvent(id, name, color, owner));
    }

    public void edit(String name, String color) {
        if (this.name.equals(name) && this.color.equals(color)) {
            return; // no change
        }
        apply(new LabelUpdatedEvent(id, name, color));
    }

    public void delete() {
        apply(new LabelDeletedEvent(id));
    }

    public void addToNode(NodeId nodeId) {
        if (labeledNodes.contains(nodeId)) {
            return; // no change
        }
        apply(new LabelAddedToNodeEvent(id, nodeId));
    }

    public void removeFromNode(NodeId nodeId) {
        if (!labeledNodes.contains(nodeId)) {
            return; // no change
        }
        apply(new LabelRemovedFromNodeEvent(id, nodeId));
    }

    @EventSourcingHandler
    public void handle(LabelCreatedEvent event) {
        id = event.getId();
        name = event.getName();
        color = event.getColor();
        owner = event.getOwner();
    }

    @EventSourcingHandler
    public void handle(LabelUpdatedEvent event) {
        name = event.getName();
        color = event.getColor();
    }

    @EventSourcingHandler
    public void handle(LabelDeletedEvent event) {
        markDeleted();
    }

    @EventSourcingHandler
    public void handle(LabelAddedToNodeEvent event) {
        labeledNodes.add(event.getNodeId());
    }

    @EventSourcingHandler
    public void handle(LabelRemovedFromNodeEvent event) {
        labeledNodes.remove(event.getNodeId());
    }
}
