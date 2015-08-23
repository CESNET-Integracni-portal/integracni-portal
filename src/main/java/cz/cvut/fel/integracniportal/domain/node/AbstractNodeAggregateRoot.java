package cz.cvut.fel.integracniportal.domain.node;

import cz.cvut.fel.integracniportal.domain.node.events.NodeMovedEvent;
import cz.cvut.fel.integracniportal.domain.node.events.NodeRenamedEvent;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.NodeId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Getter;
import org.axonframework.domain.MetaData;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

import java.util.Collections;

/**
 * @author Radek Jezdik
 */
@Getter
public class AbstractNodeAggregateRoot extends AbstractAnnotatedAggregateRoot<NodeId> {

    protected String name;

    protected FolderId parentFolder;

    protected UserId owner;

    protected String space;

    @Override
    protected void apply(Object eventPayload) {
        MetaData metaData = MetaData.from(Collections.singletonMap("space", space));
        super.apply(eventPayload, metaData);
    }

    @EventSourcingHandler
    public void onNodeRename(NodeRenamedEvent event) {
        name = event.getNewName();
    }

    @EventSourcingHandler
    public void onNodeMoved(NodeMovedEvent event) {
        parentFolder = event.getNewParent();
    }

}
