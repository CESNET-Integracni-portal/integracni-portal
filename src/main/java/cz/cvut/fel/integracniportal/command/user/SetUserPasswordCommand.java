package cz.cvut.fel.integracniportal.command.user;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserPassword;
import lombok.Value;

/**
 * Sets the user's password. The password needs to be encoded using the hashing algorithm
 * before it is dispatched.
 *
 * @author Radek Jezdik
 */
@Value
public class SetUserPasswordCommand {

    private final String token;

    private final UserPassword password;

}
