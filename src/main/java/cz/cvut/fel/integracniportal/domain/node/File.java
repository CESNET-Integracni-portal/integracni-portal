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

    public void renameFile(String newName) {
        if (newName.equals(name)) {
            return;
        }
        apply(new FileRenamedEvent(id, newName));
    }

    public void moveFile(FolderId newParent, UserId sentBy) {
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

    public void setSize(long newSize) {
        apply(new FileSizeChangedEvent(id, newSize, this.size));
    }

    public void moveFileOffline() {
        if (fileState.equals(FileState.ONLINE) == false) {
            throw new IllegalStateException("Could not move file offline, it's not online");
        }

        apply(new StartMovingFileOfflineEvent(id));
    }

    public void moveFileOnline() {
        apply(new StartMovingFileOnlineEvent(id));
    }

    public void onMoveOfflineStart(StartMovingFileOfflineEvent event) {
        fileState = FileState.MOVING_TO_OFFLINE;
    }

    @EventSourcingHandler
    public void onFileCreated(FileCreatedEvent event) {
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
    public void onSizeChanged(FileSizeChangedEvent event) {
        size = event.getNewSize();
    }

    @EventSourcingHandler
    public void onFileDeleted(FileDeletedEvent event) {
        markDeleted();
    }
}