package cz.cvut.fel.integracniportal.domain.node.events;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import lombok.EqualsAndHashCode;

/**
 * @author Radek Jezdik
 */
@EqualsAndHashCode(callSuper = true)
public class FileRenamedEvent extends NodeRenamedEvent {

    public FileRenamedEvent(FileId id, String newName) {
        super(id, newName);
    }

}
