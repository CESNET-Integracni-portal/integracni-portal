package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.Group;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.GroupRepresentation;

import java.util.List;

/**
 * @author Radek Jezdik
 */
public interface GroupService {

    public List<Group> getUserGroups(UserDetails owner);

    public Group createUserGroup(UserDetails owner, GroupRepresentation group);

    public void deleteGroup(String groupId);

    public void renameGroup(String groupId, String name);

    public void addMember(String groupId, String memberId);

    public void removeMember(String groupId, String memberId);

}
