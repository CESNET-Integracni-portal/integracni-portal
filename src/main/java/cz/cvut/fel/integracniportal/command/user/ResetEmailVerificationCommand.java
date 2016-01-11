package cz.cvut.fel.integracniportal.command.user;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

/**
 * A command that issues an email verification.
 * @author Radek Jezdik
 */
@Value
public class ResetEmailVerificationCommand {

    private final UserId userId;

}
