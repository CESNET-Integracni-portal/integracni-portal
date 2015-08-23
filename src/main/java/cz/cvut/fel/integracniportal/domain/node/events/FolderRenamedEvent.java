package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;

/**
 * @author Radek Jezdik
 */
public class FolderRenamedEvent extends NodeRenamedEvent {

    public FolderRenamedEvent(FolderId id, String newName) {
        super(id, newName);
    }

}
