package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Radek Jezdik
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class FileDeletedEvent extends NodeDeletedEvent {

    public FileDeletedEvent(FileId id) {
        super(id);
    }

}
