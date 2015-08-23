package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;

/**
 * @author Radek Jezdik
 */
public class MoveFileCommand extends MoveNodeCommand<FileId> {

    public MoveFileCommand(FileId id, FolderId newParent) {
        super(id, newParent);
    }

}
