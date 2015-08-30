package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Entity
@Table(name = "user_group", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "owner"})
})
public class Group extends AbstractEntity<String> {

    @Id
    @Column(name = "group_id")
    private String groupId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "user_id", nullable = false)
    private UserDetails owner;

    @ManyToMany
    @JoinTable(name = "user_group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserDetails> members;


    @Override
    public String getId() {
        return groupId;
    }

    @Override
    public void setId(String id) {
        groupId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDetails getOwner() {
        return owner;
    }

    public void setOwner(UserDetails owner) {
        this.owner = owner;
    }

    public Set<UserDetails> getMembers() {
        return members;
    }

    public void setMembers(Set<UserDetails> members) {
        this.members = members;
    }

}
