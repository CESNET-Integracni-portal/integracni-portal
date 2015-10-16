package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import lombok.EqualsAndHashCode;

/**
 * @author Radek Jezdik
 */
@EqualsAndHashCode(callSuper = true)
public class FolderDeletedEvent extends NodeDeletedEvent {

    public FolderDeletedEvent(FolderId id) {
        super(id);
    }

}
