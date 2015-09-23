package cz.cvut.fel.integracniportal.command.user;

import cz.cvut.fel.integracniportal.domain.Permission;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Value
public class SetUserPermissionsCommand {

    private final UserId id;

    private final Set<Permission> permissions;

}
