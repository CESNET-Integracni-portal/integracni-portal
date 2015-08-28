package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;

/**
 * @author Radek Jezdik
 */
public class FileMovedToRootEvent extends FileMovedEvent {

    private final UserId rootOwner;

    public FileMovedToRootEvent(FileId id, UserId rootOwner) {
        super(id, null);
        this.rootOwner = rootOwner;
    }

    public UserId getRootOwner() {
        return rootOwner;
    }

}
