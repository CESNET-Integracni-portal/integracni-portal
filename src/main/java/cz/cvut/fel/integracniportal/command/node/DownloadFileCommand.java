package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class DownloadFileCommand extends UserAwareCommand {

    private final FileId id;

}
