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

    public void moveFile(FolderId newParent) {
        if (parentFolder.equals(newParent)) {
            return;
        }
        apply(new FileMovedEvent(id, newParent));
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

    public void onMoveOfflineStart(StartMovingFileOfflineEvent event) {
        fileState = FileState.MOVING_TO_OFFLINE;
    }
}
