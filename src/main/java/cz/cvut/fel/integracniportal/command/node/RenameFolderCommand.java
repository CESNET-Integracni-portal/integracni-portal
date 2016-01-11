package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;

/**
 * @author Radek Jezdik
 */
public class RenameFolderCommand extends AbstractRenameNodeCommand<FolderId> {

    public RenameFolderCommand(FolderId nodeId, String newName) {
        super(nodeId, newName);
    }

}
