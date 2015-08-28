package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;

/**
 * @author Radek Jezdik
 */
public class FolderMovedToRootEvent extends FolderMovedEvent {

    private final UserId rootOwner;

    public FolderMovedToRootEvent(FolderId id, UserId rootOwner) {
        super(id, null);
        this.rootOwner = rootOwner;
    }

    public UserId getRootOwner() {
        return rootOwner;
    }

}
