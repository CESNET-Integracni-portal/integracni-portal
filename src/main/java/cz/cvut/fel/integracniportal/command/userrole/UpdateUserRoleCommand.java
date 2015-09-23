package cz.cvut.fel.integracniportal.command.userrole;

import cz.cvut.fel.integracniportal.domain.Permission;
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId;
import lombok.Value;

import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Value
public class UpdateUserRoleCommand {

    public final UserRoleId id;

    public final String name;

    public final String description;

    public final Set<Permission> permissions;

}
