package cz.cvut.fel.integracniportal.domain.group.events;

import cz.cvut.fel.integracniportal.domain.group.valueobjects.GroupId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class GroupRenamedEvent {

    private final GroupId id;

    private final String newName;

}
