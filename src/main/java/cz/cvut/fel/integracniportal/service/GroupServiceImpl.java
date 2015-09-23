package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.command.group.*;
import cz.cvut.fel.integracniportal.dao.GroupDao;
import cz.cvut.fel.integracniportal.domain.group.valueobjects.GroupId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.model.Group;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.GroupRepresentation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author Radek Jezdik
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private CommandGateway commandGateway;

    @Override
    @Transactional
    public List<Group> getUserGroups(UserDetails owner) {
        return groupDao.getUserGroups(owner);
    }

    @Override
    public Group createUserGroup(UserDetails owner, GroupRepresentation groupRepresentation) {
        String id = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new CreateGroupCommand(
                GroupId.of(id),
                groupRepresentation.getName()
        ));

        return groupDao.get(id);
    }

    @Override
    public void deleteGroup(String groupId) {
        commandGateway.sendAndWait(new DeleteGroupCommand(
                GroupId.of(groupId)
        ));
    }

    @Override
    public void renameGroup(String groupId, String name) {
        commandGateway.sendAndWait(new RenameGroupCommand(
                GroupId.of(groupId),
                name
        ));
    }

    @Override
    public void addMember(String groupId, String memberId) {
        commandGateway.sendAndWait(new AddUserToGroupCommand(
                GroupId.of(groupId),
                UserId.of(memberId)
        ));
    }

    @Override
    public void removeMember(String groupId, String memberId) {
        commandGateway.sendAndWait(new RemoveUserFromGroupCommand(
                GroupId.of(groupId),
                UserId.of(memberId)
        ));
    }

}
