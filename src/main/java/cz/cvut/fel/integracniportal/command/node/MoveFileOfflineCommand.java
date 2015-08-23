package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class MoveFileOfflineCommand {

    private FileId id;

}
