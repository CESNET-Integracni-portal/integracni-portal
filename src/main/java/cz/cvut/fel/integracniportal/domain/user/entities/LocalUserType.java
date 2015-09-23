package cz.cvut.fel.integracniportal.domain.user.entities;

import cz.cvut.fel.integracniportal.domain.user.User;
import cz.cvut.fel.integracniportal.domain.user.events.UserEmailVerificationStartedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserEmailVerifiedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserPasswordResetStartedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserPasswordSetEvent;
import cz.cvut.fel.integracniportal.exceptions.IllegalOperationException;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

/**
 * @author Radek Jezdik
 */
public class LocalUserType extends UserType {

    private boolean verified;

    @EventSourcingHandler
    public void handle(UserEmailVerifiedEvent event) {
        verified = true;
    }

    @Override
    public void setPassword(User user) {
        if (verified == false) {
            apply(new UserEmailVerifiedEvent(user.getId()));
        }
        apply(new UserPasswordSetEvent(user.getId()));
    }

    @Override
    public void startVerification(User user) {
        if (verified) {
            throw new IllegalOperationException("Already verified");
        }
        apply(new UserEmailVerificationStartedEvent(user.getId()));
    }

    @Override
    public void startPasswordReset(User user) {
        if (!verified) {
            throw new IllegalOperationException("Cannot reset password if not verified");
        }
        apply(new UserPasswordResetStartedEvent(user.getId()));
    }

}
