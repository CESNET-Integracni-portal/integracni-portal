package cz.cvut.fel.integracniportal.domain.group;

import cz.cvut.fel.integracniportal.command.group.*;
import cz.cvut.fel.integracniportal.dao.GroupDao;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
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
public class GroupCommandHandler {

    @Resource(name = "groupAggregateRepository")
    private Repository<Group> repository;

    @Autowired
    private GroupDao groupDao;

    @CommandHandler
    public void handle(CreateGroupCommand command) {
        createGroup(command);
    }

    @CommandHandler
    public void handle(RenameGroupCommand command) {
        renameGroup(command);
    }

    @CommandHandler
    public void handle(DeleteGroupCommand command) {
        deleteGroup(command);
    }

    @CommandHandler
    public void handle(AddUserToGroupCommand command) {
        addUserToGroup(command);
    }

    @CommandHandler
    public void handle(RemoveUserFromGroupCommand command) {
        removeUserFromGroup(command);
    }

    private void createGroup(CreateGroupCommand command) {
        checkUnique(command.getSentBy(), command.getName());

        Group group = new Group(
                command.getId(),
                command.getName(),
                command.getSentBy()
        );

        repository.add(group);
    }

    private void renameGroup(RenameGroupCommand command) {
        Group group = repository.load(command.getId());

        checkUnique(group.getOwner(), command.getNewName());

        group.rename(command.getNewName());
    }

    private void deleteGroup(DeleteGroupCommand command) {
        Group group = repository.load(command.getId());
        group.delete();
    }

    private void addUserToGroup(AddUserToGroupCommand command) {
        Group group = repository.load(command.getId());
        group.addUser(command.getUser());
    }

    private void removeUserFromGroup(RemoveUserFromGroupCommand command) {
        Group group = repository.load(command.getId());
        group.removeUser(command.getUser());
    }

    private void checkUnique(UserId owner, String name) {
        if (groupDao.groupExists(owner.getId(), name)) {
            throw new AlreadyExistsException("group.alreadyExists");
        }
    }

}
