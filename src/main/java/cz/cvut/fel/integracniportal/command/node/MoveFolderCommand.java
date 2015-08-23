package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;

/**
 * @author Radek Jezdik
 */
public class MoveFolderCommand extends MoveNodeCommand<FolderId> {

    public MoveFolderCommand(FolderId id, FolderId newParent) {
        super(id, newParent);
    }

}
