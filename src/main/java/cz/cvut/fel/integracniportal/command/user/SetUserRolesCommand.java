package cz.cvut.fel.integracniportal.command.user;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId;
import lombok.Value;

import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Value
public class SetUserRolesCommand {

    private final UserId id;

    private final Set<UserRoleId> roles;

}
