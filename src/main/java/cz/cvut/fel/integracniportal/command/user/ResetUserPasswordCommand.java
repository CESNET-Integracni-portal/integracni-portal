package cz.cvut.fel.integracniportal.command.user;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

/**
 * A command that issues a users password reset.
 * @author Radek Jezdik
 */
@Value
public class ResetUserPasswordCommand {

    private final UserId userId;

}
