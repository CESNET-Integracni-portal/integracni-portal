package cz.cvut.fel.integracniportal.domain.user.saga;

import cz.cvut.fel.integracniportal.domain.user.events.UserPasswordResetStartedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserPasswordSetEvent;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;


/**
 * @author Radek Jezdik
 */
public class RestoreLostUserPasswordSaga extends AbstractExpirableUserTokenSaga {

    @StartSaga
    @SagaEventHandler(associationProperty = "userId")
    public void handle(UserPasswordResetStartedEvent event) {
        createTokenAndScheduleExpireEvent(event.getUserId());

        // TODO send token via email
//        UserDetails user = userDao.getUserById(event.getUserId().getId());
//        user.getEmail();
    }

    @SagaEventHandler(associationProperty = "userId")
    public void handle(UserPasswordSetEvent event) {
        end();
    }

}
