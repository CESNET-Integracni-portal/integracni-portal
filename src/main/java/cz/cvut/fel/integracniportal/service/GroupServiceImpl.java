package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.GroupDao;
import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.Group;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDetailsDao userService;

    public Group getGroup(Long groupId) {
        Group group = groupDao.get(groupId);
        if (group == null) {
            throw new NotFoundException("group.notFound.id", groupId);
        }
        return group;
    }

    @Override
    public Group getGroupById(Long groupId) {
        return groupDao.get(groupId);
    }

    @Override
    public List<Group> getUserGroups(UserDetails owner) {
        return groupDao.getUserGroups(owner);
    }

    @Override
    public Group createUserGroup(UserDetails owner, GroupRepresentation groupRepresentation) {
        Group group = new Group();
        group.setName(groupRepresentation.getName());
        group.setOwner(owner);

        save(group);

        return group;
    }

    @Override
    public void deleteGroup(Long groupId) {
        Group group = getGroup(groupId);
        groupDao.delete(group);
    }

    @Override
    public void renameGroup(Long groupId, String name) {
        Group group = getGroup(groupId);
        group.setName(name);

        save(group);
    }

    @Override
    public void addMember(Long groupId, Long memberId) {
        UserDetails user = userService.getUserById(memberId);
        Group group = getGroup(groupId);

        Set<UserDetails> members = group.getMembers();
        if (members == null) {
            members = new HashSet<UserDetails>();
            group.setMembers(members);
        }

        members.add(user);
        save(group);
    }

    @Override
    public void removeMember(Long groupId, Long memberId) {
        UserDetails user = userService.getUserById(memberId);
        Group group = getGroup(groupId);

        Set<UserDetails> members = group.getMembers();
        if (members == null) {
            return;
        }

        members.remove(user);
        save(group);
    }

    protected void save(Group group) {
        groupDao.save(group);
    }

}
