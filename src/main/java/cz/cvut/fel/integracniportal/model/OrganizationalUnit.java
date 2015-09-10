package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "organization_unit")
public class OrganizationalUnit extends AbstractEntity<String> {

    @Id
    @Column(name = "unit_id")
    private String unitId;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Long size;

    @ManyToMany
    @JoinTable(
            name = "organization_unit_admins",
            joinColumns = @JoinColumn(name = "unit_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserDetails> admins;

    @OneToMany(mappedBy = "organizationalUnit")
    private Set<UserDetails> members;

    @Override
    public String getId() {
        return unitId;
    }

    @Override
    public void setId(String id) {
        this.unitId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Set<UserDetails> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<UserDetails> admins) {
        this.admins = admins;
    }

    public Set<UserDetails> getMembers() {
        return members;
    }

    public void setMembers(Set<UserDetails> members) {
        this.members = members;
    }
}
