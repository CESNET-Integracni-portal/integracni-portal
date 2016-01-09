package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class MoveFolderOfflineCommand {

    private FolderId id;

}
