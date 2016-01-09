package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class FileContentsChangedEvent {

    private final FileId id;

    private final long newSize;

    private final long originalSize;

}
