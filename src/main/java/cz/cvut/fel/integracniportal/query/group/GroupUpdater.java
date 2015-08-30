package cz.cvut.fel.integracniportal.query.group;

import cz.cvut.fel.integracniportal.dao.GroupDao;
import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.domain.group.events.*;
import cz.cvut.fel.integracniportal.model.Group;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * @author Radek Jezdik
 */
@Component
public class GroupUpdater {

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDetailsDao userDao;

    @EventHandler
    public void createGroup(GroupCreatedEvent event) {
        UserDetails owner = userDao.getReference(event.getOwner().getId());

        Group group = new Group();
        group.setId(event.getId().getId());
        group.setOwner(owner);
        group.setName(event.getName());

        groupDao.save(group);
    }

    @EventHandler
    public void renameGroup(GroupRenamedEvent event) {
        Group group = groupDao.load(event.getId().getId());
        group.setName(event.getNewName());
        groupDao.save(group);
    }

    @EventHandler
    public void deleteGroup(GroupDeletedEvent event) {
        Group group = groupDao.load(event.getId().getId());
        groupDao.delete(group);
    }

    @EventHandler
    public void addUser(UserAddedToGroupEvent event) {
        Group group = groupDao.load(event.getId().getId());

        if (group.getMembers() == null) {
            group.setMembers(new HashSet<UserDetails>());
        }

        group.getMembers().add(userDao.getReference(event.getUser().getId()));

        groupDao.save(group);
    }

    @EventHandler
    public void removeUser(UserRemovedFromGroupEvent event) {
        Group group = groupDao.load(event.getId().getId());

        if (group.getMembers() == null) {
            return;
        }

        group.getMembers().remove(userDao.getReference(event.getUser().getId()));

        groupDao.save(group);
    }

}
