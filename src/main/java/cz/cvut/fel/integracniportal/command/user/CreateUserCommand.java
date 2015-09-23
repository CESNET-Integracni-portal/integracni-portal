package cz.cvut.fel.integracniportal.command.user;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId;
import lombok.Value;

import java.util.Collections;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Value
public class CreateUserCommand {

    private final UserId id;

    private final String username;

    private final String email;

    private final Set<UserRoleId> roles;

    public CreateUserCommand(UserId id, String username, String email) {
        this(id, username, email, Collections.<UserRoleId>emptySet());
    }

    public CreateUserCommand(UserId id, String username, String email, Set<UserRoleId> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = Collections.unmodifiableSet(roles);
    }

}
