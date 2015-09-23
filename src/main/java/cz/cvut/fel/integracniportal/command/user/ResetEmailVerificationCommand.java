package cz.cvut.fel.integracniportal.command.user;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class ResetEmailVerificationCommand {

    private final UserId userId;

}
