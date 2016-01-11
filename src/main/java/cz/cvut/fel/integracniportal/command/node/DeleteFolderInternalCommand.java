package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import lombok.Value;

/**
 * Used to actually delete the folder after it was cleared of
 * all child files and folders by {@link cz.cvut.fel.integracniportal.domain.node.saga.FolderDeletionSaga}
 *
 * @author Radek Jezdik
 */
@Value
public class DeleteFolderInternalCommand {

    private final FolderId id;

}
