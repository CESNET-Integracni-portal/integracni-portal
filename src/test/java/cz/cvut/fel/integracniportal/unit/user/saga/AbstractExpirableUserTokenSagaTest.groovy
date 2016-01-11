package cz.cvut.fel.integracniportal.unit.user.saga

import cz.cvut.fel.integracniportal.dao.UserTokenDao
import cz.cvut.fel.integracniportal.domain.user.saga.AbstractExpirableUserTokenSaga
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId
import cz.cvut.fel.integracniportal.service.SecureRandomGenerator
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.test.saga.AnnotatedSagaTestFixture
import org.joda.time.Duration
import spock.lang.Specification

/**
 * @author Radek Jezdik
 */
abstract class AbstractExpirableUserTokenSagaTest extends Specification {

    AnnotatedSagaTestFixture fixture

    UserTokenDao userTokenDao

    SecureRandomGenerator randomGenerator

    final expire = 60
    final token = "abc123"

    final user = UserId.of("1")


    abstract Class getSagaClass()

    abstract getStartEvent()

    abstract getEndEvent()


    def setup() {
        userTokenDao = Mock(UserTokenDao)
        randomGenerator = Mock(SecureRandomGenerator)

        fixture = new AnnotatedSagaTestFixture(getSagaClass())
        fixture.registerResource(userTokenDao)
        fixture.registerResource(randomGenerator)
        fixture.registerCommandGateway(CommandGateway)
    }

    def "creates token and schedules the expire event"() {
        1 * randomGenerator.generate() >> token
        1 * userTokenDao.addToken(user.getId(), token)

        expect:
            fixture.whenAggregate(user)
                    .publishes(getStartEvent())

                    .expectScheduledEvent(
                        Duration.standardMinutes(expire),
                        new AbstractExpirableUserTokenSaga.TokenExpiredEvent(token))
                    .expectAssociationWith("token", token)
    }

    def "if token expired, remove it and end saga"() {
        1 * randomGenerator.generate() >> token
        1 * userTokenDao.addToken(user.getId(), token)
        1 * userTokenDao.deleteToken(user.getId())

        expect:
            fixture.givenAggregate(user)
                    .published(getStartEvent())

                    .whenTimeElapses(Duration.standardMinutes(60))

                    .expectActiveSagas(0)
    }

    def "if user verified, end saga"() {
        1 * randomGenerator.generate() >> token
        1 * userTokenDao.addToken(user.getId(), token)
        1 * userTokenDao.deleteToken(user.getId())

        expect:
            fixture.givenAggregate(user)
                    .published(getStartEvent())
                    .andThenTimeElapses(Duration.standardMinutes(30))

                    .whenAggregate(user).publishes(getEndEvent())

                    .expectActiveSagas(0)
    }

}
