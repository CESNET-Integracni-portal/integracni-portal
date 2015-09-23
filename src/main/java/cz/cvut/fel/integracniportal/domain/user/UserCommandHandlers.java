package cz.cvut.fel.integracniportal.domain.user;

import cz.cvut.fel.integracniportal.command.user.*;
import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.dao.UserTokenDao;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Radek Jezdik
 */
@Component
public class UserCommandHandlers {

    @Resource(name = "userAggregateRepository")
    private Repository<User> repository;

    @Autowired
    private UserDetailsDao userDao;

    @Autowired
    private UserTokenDao userTokenDao;

    @CommandHandler
    public void handle(CreateUserCommand command) {
        User user = new User(
                command.getId(),
                command.getUsername(),
                command.getEmail(),
                command.getRoles());

        checkUnique(user.getUsername(), user.getEmail());

        repository.add(user);
    }

    @CommandHandler
    public void handle(ResetEmailVerificationCommand command) {
        User user = repository.load(command.getUserId());
        user.startVerificationReset();
    }

    @CommandHandler
    public void handle(ResetUserPasswordCommand command) {
        User user = repository.load(command.getUserId());
        user.startPasswordReset();
    }

    @CommandHandler
    public void handle(SetUserPasswordCommand command) {
        String userId = userTokenDao.getUserIdByToken(command.getToken());

        if (userId == null) {
            throw new NotFoundException("token.notFound");
        }

        User user = repository.load(UserId.of(userId));
        user.setPassword();

        // we set the password here, for security reasons
        // events must not contain password (event sourced events would contain old passwords!)
        UserDetails userDetails = userDao.getReference(userId);
        userDetails.setPassword(command.getEncodedPassword());
        userDao.save(userDetails);
    }

    @CommandHandler
    public void handle(SetUserRolesCommand command) {
        User user = repository.load(command.getId());
        user.setRoles(command.getRoles());
    }

    @CommandHandler
    public void handle(SetUserPermissionsCommand command) {
        User user = repository.load(command.getId());
        user.setPermissions(command.getPermissions());
    }

    private void checkUnique(String username, String email) {
        if (userDao.usernameExists(username)) {
            throw new AlreadyExistsException("user.username.alreadyExists");
        }
        if (userDao.emailExists(email)) {
            throw new AlreadyExistsException("user.email.alreadyExists");
        }
    }

}
