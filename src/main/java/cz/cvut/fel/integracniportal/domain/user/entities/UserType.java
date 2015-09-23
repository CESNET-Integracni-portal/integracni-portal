package cz.cvut.fel.integracniportal.domain.user.entities;

import cz.cvut.fel.integracniportal.domain.user.User;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedEntity;

/**
 * @author Radek Jezdik
 */
public abstract class UserType extends AbstractAnnotatedEntity {

    public abstract void setPassword(User user);

    public abstract void startVerification(User user);

    public abstract void startPasswordReset(User user);

}
