package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;

/**
 * @author Radek Jezdik
 */
public class RenameFileCommand extends AbstractRenameNodeCommand<FileId> {

    public RenameFileCommand(FileId nodeId, String newName) {
        super(nodeId, newName);
    }

}
