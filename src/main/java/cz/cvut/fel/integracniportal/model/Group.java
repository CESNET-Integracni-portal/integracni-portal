package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Entity
@Table(name = "group", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "owner"})
})
public class Group extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    @Column(name = "group_id")
    private Long groupId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "user_id", nullable = false)
    private UserDetails owner;

    @ManyToMany
    private Set<UserDetails> members;


    @Override
    public Long getId() {
        return groupId;
    }

    @Override
    public void setId(Long id) {
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
