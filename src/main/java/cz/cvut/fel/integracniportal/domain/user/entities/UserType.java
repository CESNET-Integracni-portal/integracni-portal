package cz.cvut.fel.integracniportal.domain.user.entities;

import cz.cvut.fel.integracniportal.domain.user.User;
import cz.cvut.fel.integracniportal.domain.user.events.UserEmailVerificationStartedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserPasswordResetStartedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserPasswordSetEvent;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedEntity;

/**
 * This abstract entity is part of the state pattern for User aggregate. Because, there can
 * be multiple types of users (local, from external sources, etc.), they handle some things differently.
 * The subclasses need to implement the abstract methods, and publish appropriate events.
 *
 * @author Radek Jezdik
 */
public abstract class UserType extends AbstractAnnotatedEntity {

    /**
     * Handles setting password for the user. If setting password is supported by this type,
     * publish {@link UserPasswordSetEvent}.
     * @param user the user entity for which the password was set
     */
    public abstract void setPassword(User user);

    /**
     * Handles the start of the verification process. If needed to verify,
     * publish {@link UserEmailVerificationStartedEvent}.
     *
     * @param user the user entity for which the verification is handled
     */
    public abstract void startVerification(User user);

    /**
     * Handles the start of the password reset process. If password reset is supported by this type,
     * publish {@link UserPasswordResetStartedEvent}.
     *
     * @param user
     */
    public abstract void startPasswordReset(User user);

}
