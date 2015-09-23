package cz.cvut.fel.integracniportal.command.user;

import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class SetUserPasswordCommand {

    private final String token;

    private final String encodedPassword;

}
