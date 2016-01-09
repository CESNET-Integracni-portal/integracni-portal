package cz.cvut.fel.integracniportal.domain.node;

import cz.cvut.fel.integracniportal.domain.node.events.*;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileState;
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
public class File extends AbstractNodeAggregateRoot {

    @AggregateIdentifier
    private FileId id;

    private long size;

    private String mimetype;

    private FileState fileState;

    public File(FileId id, String name, FolderId parentFolder, UserId owner, String space, long size, String mimetype, FileState fileState) {
        apply(new FileCreatedEvent(
                id,
                name,
                parentFolder,
                owner,
                space,
                size,
                mimetype,
                fileState));
    }

    public void rename(String newName) {
        if (newName.equals(name)) {
            return;
        }
        apply(new FileRenamedEvent(id, newName));
    }

    public void move(FolderId newParent, UserId sentBy) {
        if (parentFolder == null && newParent == null) {
            return;
        }
        if (parentFolder != null && parentFolder.equals(newParent)) {
            return;
        }

        FileMovedEvent event = (newParent != null)
                ? new FileMovedEvent(id, newParent)
                : new FileMovedToRootEvent(id, sentBy);

        apply(event);
    }

    public void delete() {
        if (!isDeleted()) {
            apply(new FileDeletedEvent(id));
        }
    }

    public void updateContents(long newSize) {
        moveOnline(); // automatically moved online in file repository when downloaded
        apply(new FileContentsChangedEvent(id, newSize, this.size));
    }

    public void moveOffline() {
        if (fileState.equals(FileState.ONLINE) == false) {
            return;
        }
        apply(new FileMovedOfflineEvent(id));
    }

    public void moveOnline() {
        if (fileState.equals(FileState.ONLINE) == false) {
            return;
        }
        apply(new FileMovedOnlineEvent(id));
    }

    public void download() {
        moveOnline(); // automatically moved online in file repository when downloaded
        apply(new FileDownloadedEvent(id));
    }

    @EventSourcingHandler
    public void handle(FileMovedOnlineEvent event) {
        fileState = FileState.ONLINE;
    }

    @EventSourcingHandler
    public void handle(FileMovedOfflineEvent event) {
        fileState = FileState.OFFLINE;
    }

    @EventSourcingHandler
    public void handle(FileCreatedEvent event) {
        id = event.getId();
        name = event.getName();
        parentFolder = event.getParentFolder();
        owner = event.getOwner();
        space = event.getSpace();
        size = event.getSize();
        mimetype = event.getMimetype();
        fileState = event.getFileState();
    }

    @EventSourcingHandler
    public void handle(FileContentsChangedEvent event) {
        size = event.getNewSize();
    }

    @EventSourcingHandler
    public void handle(FileDeletedEvent event) {
        markDeleted();
    }
}
