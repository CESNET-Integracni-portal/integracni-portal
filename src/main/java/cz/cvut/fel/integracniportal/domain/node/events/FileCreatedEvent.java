package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileState;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class FileCreatedEvent extends AbstractNodeEvent {

    protected FileId id;

    private final long size;

    private final String mimetype;

    private final FileState fileState;

    public FileCreatedEvent(FileId id, String name, FolderId parentFolder, UserId owner, String space, long size, String mimetype, FileState fileState) {
        super(name, parentFolder, owner, space);
        this.id = id;
        this.size = size;
        this.mimetype = mimetype;
        this.fileState = fileState;
    }
}
