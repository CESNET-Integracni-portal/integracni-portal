package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class FolderCreatedEvent extends NodeCreatedEvent<FolderId> {

    public FolderCreatedEvent(FolderId id, String name, FolderId parentFolder, UserId owner, String space) {
        super(id, name, parentFolder, owner, space);
    }

}