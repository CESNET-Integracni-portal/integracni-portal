package cz.cvut.fel.integracniportal.command.group;

import cz.cvut.fel.integracniportal.command.node.UserAwareCommand;
import cz.cvut.fel.integracniportal.domain.group.valueobjects.GroupId;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class CreateGroupCommand extends UserAwareCommand {

    private final GroupId id;

    private final String name;

}
