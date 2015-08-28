package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;

/**
 * @author Radek Jezdik
 */
public class UserAwareCommand {

    private UserId sentBy;

    public UserId getSentBy() {
        return sentBy;
    }

    public void setSendBy(UserId userId) {
        sentBy = userId;
    }

}
