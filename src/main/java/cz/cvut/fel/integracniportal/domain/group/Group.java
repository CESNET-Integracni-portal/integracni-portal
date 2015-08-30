package cz.cvut.fel.integracniportal.domain.group;

import cz.cvut.fel.integracniportal.domain.group.events.*;
import cz.cvut.fel.integracniportal.domain.group.valueobjects.GroupId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@NoArgsConstructor
@Getter
public class Group extends AbstractAnnotatedAggregateRoot<GroupId> {

    @AggregateIdentifier
    private GroupId id;

    private UserId owner;

    private String name;

    private String color;

    private Set<UserId> members = new HashSet<UserId>();

    public Group(GroupId id, String name, UserId owner) {
        apply(new GroupCreatedEvent(id, name, owner));
    }

    public void rename(String newName) {
        if (name.equals(newName)) {
            return;
        }
        apply(new GroupRenamedEvent(id, newName));
    }

    public void delete() {
        apply(new GroupDeletedEvent(id));
    }

    public void addUser(UserId user) {
        if (members.contains(user)) {
            return;
        }
        apply(new UserAddedToGroupEvent(id, user));
    }

    public void removeUser(UserId user) {
        if (members.contains(user) == false) {
            return;
        }
        apply(new UserRemovedFromGroupEvent(id, user));
    }

    @EventSourcingHandler
    public void onGroupCreated(GroupCreatedEvent event) {
        id = event.getId();
        name = event.getName();
        owner = event.getOwner();
    }

    @EventSourcingHandler
    public void onGroupRenamed(GroupRenamedEvent event) {
        name = event.getNewName();
    }

    @EventSourcingHandler
    public void onGroupDeleted(GroupDeletedEvent event) {
        members.clear();
        markDeleted();
    }

    @EventSourcingHandler
    public void onUserAdded(UserAddedToGroupEvent event) {
        members.add(event.getUser());
    }

    @EventSourcingHandler
    public void onUserAdded(UserRemovedFromGroupEvent event) {
        members.remove(event.getUser());
    }

}
