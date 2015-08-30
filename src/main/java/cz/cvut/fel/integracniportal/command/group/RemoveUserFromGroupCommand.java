package cz.cvut.fel.integracniportal.command.group;

import cz.cvut.fel.integracniportal.command.node.UserAwareCommand;
import cz.cvut.fel.integracniportal.domain.group.valueobjects.GroupId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class RemoveUserFromGroupCommand extends UserAwareCommand {

    private final GroupId id;

    private final UserId user;

}