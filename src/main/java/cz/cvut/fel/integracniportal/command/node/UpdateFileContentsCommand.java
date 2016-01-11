package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.service.FileUpload;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Radek Jezdik
 */

@Getter
@AllArgsConstructor
public class UpdateFileContentsCommand extends UserAwareCommand {

    private final FileId id;

    private final FileUpload fileUpload;

}
