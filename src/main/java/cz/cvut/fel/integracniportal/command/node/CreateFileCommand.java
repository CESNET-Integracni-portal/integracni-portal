package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileState;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Optional;

/**
 * @author Radek Jezdik
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class CreateFileCommand extends AbstractNodeCommand<FileId> {

    private long size;

    private String mimetype;

    private Optional<FileState> fileState;

    public CreateFileCommand(FileId id, String name, FolderId parentFolder, UserId owner, String space, long size, String mimetype, Optional<FileState> fileState) {
        super(id, name, parentFolder, owner, space);
        this.size = size;
        this.mimetype = mimetype;
        this.fileState = fileState;
    }
}
