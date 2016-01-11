package cz.cvut.fel.integracniportal.domain.user.saga;

import cz.cvut.fel.integracniportal.dao.UserTokenDao;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.service.SecureRandomGenerator;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * An abstract saga class that manages user token expiration for implementing sagas.
 * Implementing sagas should call {@link #createTokenAndScheduleExpireEvent(UserId)} that
 * creates the token and schedules an event for token expiration.
 * They should call {@link #end()} when the token is not needed anymore, i.e. user used his token to
 * do some task and the token should be removed before it expires.
 *
 * @author Radek Jezdik
 */
public abstract class AbstractExpirableUserTokenSaga extends AbstractAnnotatedSaga {

    private static class TokenExpiredEvent {

        private final String token;

        private TokenExpiredEvent(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }

    private final static long EXPIRE_IN_MINUTES = 60;

    @Autowired
    protected transient UserTokenDao userTokenDao;

    @Autowired
    protected transient EventScheduler scheduler;

    @Autowired
    protected transient SecureRandomGenerator randomGenerator;

    private ScheduleToken timeout;

    private UserId userId;

    /**
     * Creates and persists a new random token for the given user and schedules
     * an expire event to be published. Returns the created token.
     *
     * @param userId id of the user to create a token for
     * @return token
     */
    protected String createTokenAndScheduleExpireEvent(UserId userId) {
        String token = randomGenerator.generate();
        userTokenDao.addToken(userId.getId(), token);

        this.userId = userId;
        associateWith("token", token);

        timeout = scheduler.schedule(
                Duration.standardMinutes(EXPIRE_IN_MINUTES),
                new TokenExpiredEvent(token));

        return token;
    }

    @SagaEventHandler(associationProperty = "token")
    public void handle(TokenExpiredEvent event) {
        end();
    }

    @Override
    protected void end() {
        userTokenDao.deleteToken(userId.getId());
        scheduler.cancelSchedule(timeout);
        super.end();
    }

    public void setScheduler(EventScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setUserTokenDao(UserTokenDao userTokenDao) {
        this.userTokenDao = userTokenDao;
    }

    public void setRandomGenerator(SecureRandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

}
