package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;

/**
 * A class that can be extended so the command carries the ID
 * of the user who sent the command.
 *
 * See {@link AddUserCommandDispatchInterceptor}, an interceptor which
 * automatically sets the current user to the command that is an instance
 * of this class.
 *
 * @author Radek Jezdik
 */
public abstract class UserAwareCommand {

    private UserId sentBy;

    /**
     * Returns the ID of the user who sent this command.
     *
     * @return the user ID
     */
    public UserId getSentBy() {
        return sentBy;
    }

    /**
     * Sets the ID of the user who sent this command.
     *
     * @param userId the user ID
     */
    public void setSendBy(UserId userId) {
        sentBy = userId;
    }

}
