package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class FolderDeletionStartedEvent {

    private final FolderId id;

}
