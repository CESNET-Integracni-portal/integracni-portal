package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import lombok.EqualsAndHashCode;

/**
 * @author Radek Jezdik
 */
@EqualsAndHashCode(callSuper = true)
public class FileMovedEvent extends NodeMovedEvent {

    public FileMovedEvent(FileId id, FolderId newParent) {
        super(id, newParent);
    }

}
