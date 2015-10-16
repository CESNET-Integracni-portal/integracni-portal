package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileState;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.service.FileUpload;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Optional;

/**
 * @author Radek Jezdik
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class CreateFileCommand extends AbstractNodeCommand<FileId> {

    private FileUpload fileUpload;

    private Optional<FileState> fileState;

    public CreateFileCommand(FileId id, FileUpload file, FolderId parentFolder, UserId owner, String space, Optional<FileState> fileState) {
        super(id, file.getFileName(), parentFolder, owner, space);
        this.fileUpload = file;
        this.fileState = fileState;
    }

}
