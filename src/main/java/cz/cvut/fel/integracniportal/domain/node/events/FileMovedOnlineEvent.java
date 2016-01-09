package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class FileMovedOnlineEvent {

    private FileId id;

}
