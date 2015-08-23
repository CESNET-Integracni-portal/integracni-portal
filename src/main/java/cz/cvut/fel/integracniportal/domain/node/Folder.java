package cz.cvut.fel.integracniportal.domain.node;

import cz.cvut.fel.integracniportal.domain.node.events.FolderCreatedEvent;
import cz.cvut.fel.integracniportal.domain.node.events.FolderMovedEvent;
import cz.cvut.fel.integracniportal.domain.node.events.FolderRenamedEvent;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

/**
 * @author Radek Jezdik
 */
@Getter
@NoArgsConstructor
public class Folder extends AbstractNodeAggregateRoot {

    @AggregateIdentifier
    private FolderId id;

    public Folder(FolderId id, String name, FolderId parentFolder, UserId owner, String space) {
        apply(new FolderCreatedEvent(
                id,
                name,
                parentFolder,
                owner,
                space));
    }

    public void renameFolder(String newName) {
        if (!name.equals(newName)) {
            apply(new FolderRenamedEvent(id, newName));
        }
    }

    public void moveFolder(FolderId newParent) {
        if (!parentFolder.equals(newParent)) {
            apply(new FolderMovedEvent(id, newParent));
        }
    }

    @EventSourcingHandler
    public void onFolderCreated(FolderCreatedEvent event) {
        id = event.getId();
        name = event.getName();
        parentFolder = event.getParentFolder();
        owner = event.getOwner();
        space = event.getSpace();
    }
}
