package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;

/**
 * @author Radek Jezdik
 */
public class FolderMovedEvent extends NodeMovedEvent {

    public FolderMovedEvent(FolderId id, FolderId newParent) {
        super(id, newParent);
    }

}
