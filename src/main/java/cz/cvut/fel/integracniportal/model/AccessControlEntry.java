package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Access Control Entry.
 * <p>
 * Holds the Set of AccessControlPermission objects, describing available actions for targetUser on targetNode.
 *
 * @author Eldar Iosip
 */
@Entity
@Table(name = "access_control_entry")
public class AccessControlEntry extends AbstractEntity<Long> {

    @Id
    @Column(name = "permission_id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long permissionId;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    private UserDetails owner;

    @ManyToOne
    @JoinColumn(name = "target_user_id", referencedColumnName = "user_id")
    private UserDetails targetUser;

    @ManyToOne
    @JoinColumn(name = "target_group_id", referencedColumnName = "group_id")
    private Group targetGroup;

    @ManyToOne
    @JoinColumn(name = "node_id")
    private Node targetNode;

    @Column(name = "node_permissions")
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<AccessControlPermission> accessControlPermissions;

    public AccessControlEntry() {
        this.accessControlPermissions = new HashSet<AccessControlPermission>();
    }

    @Override
    public Long getId() {
        return permissionId;
    }

    @Override
    public void setId(Long id) {
        this.permissionId = id;
    }

    public UserDetails getOwner() {
        return owner;
    }

    public void setOwner(UserDetails owner) {
        this.owner = owner;
    }

    public UserDetails getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(UserDetails targetUser) {
        this.targetUser = targetUser;
    }

    public Group getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(Group targetGroup) {
        this.targetGroup = targetGroup;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    public Set<AccessControlPermission> getAccessControlPermissions() {
        return accessControlPermissions;
    }

    public void setAccessControlPermissions(Set<AccessControlPermission> accessControlPermissions) {
        this.accessControlPermissions = accessControlPermissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessControlEntry entry = (AccessControlEntry) o;
        return Objects.equals(permissionId, entry.permissionId) &&
                Objects.equals(getOwner(), entry.getOwner()) &&
                Objects.equals(getTargetUser(), entry.getTargetUser()) &&
                Objects.equals(getTargetGroup(), entry.getTargetGroup()) &&
                Objects.equals(getTargetNode(), entry.getTargetNode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), getTargetUser(), getTargetGroup(), getTargetNode());
    }
}
