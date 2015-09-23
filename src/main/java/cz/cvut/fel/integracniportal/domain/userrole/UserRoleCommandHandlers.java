package cz.cvut.fel.integracniportal.domain.userrole;

import cz.cvut.fel.integracniportal.command.userrole.CreateUserRoleCommand;
import cz.cvut.fel.integracniportal.command.userrole.UpdateUserRoleCommand;
import cz.cvut.fel.integracniportal.dao.UserRoleDao;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Radek Jezdik
 */
@Component
public class UserRoleCommandHandlers {

    @Resource(name = "userRoleAggregateRepository")
    private Repository<UserRole> repository;

    @Autowired
    private UserRoleDao userRoleDao;

    @CommandHandler
    public void handle(CreateUserRoleCommand command) {
        UserRole userRole = new UserRole(
                command.getId(),
                command.getName(),
                command.getDescription(),
                command.getPermissions());

        checkUnique(userRole.getName());

        repository.add(userRole);
    }

    @CommandHandler
    public void handle(UpdateUserRoleCommand command) {
        UserRole role = repository.load(command.getId());

        role.update(command.getName(), command.getDescription(), command.getPermissions());
    }

    private void checkUnique(String name) {
        if (userRoleDao.userRoleExists(name)) {
            throw new AlreadyExistsException("userRole.alreadyExists");
        }
    }

}
