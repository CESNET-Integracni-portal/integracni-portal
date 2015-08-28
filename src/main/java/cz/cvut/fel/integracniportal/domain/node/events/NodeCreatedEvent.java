package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.NodeId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Radek Jezdik
 */
@Getter
@AllArgsConstructor
public class NodeCreatedEvent<T extends NodeId> {

    protected T id;

    protected String name;

    protected FolderId parentFolder;

    protected UserId owner;

    protected String space;

}
