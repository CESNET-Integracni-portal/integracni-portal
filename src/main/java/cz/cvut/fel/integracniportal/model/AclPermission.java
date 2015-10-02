package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Permission object.
 * <p>
 * It's holding the user's allowed actions,
 * which modify the FSNode state
 *
 * @author Eldar Iosip
 */
public class AclPermission extends AbstractEntity<Long> {

    @Id
    @Column(name = "acl_id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long aclId;

    @Column(name = "node_permissions")
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<NodePermission> nodePermissions;

    @ManyToOne
    @JoinColumn(name = "target_user", referencedColumnName = "user_id", nullable = false)
    private AbstractUser targetUser;

    @ManyToOne
    @JoinColumn(name = "node", referencedColumnName = "node_id", nullable = false)
    private AbstractNode node;

    public AclPermission() {
        this.nodePermissions = new HashSet<NodePermission>();
    }

    @Override
    public Long getId() {
        return aclId;
    }

    @Override
    public void setId(Long aclId) {
        this.aclId = aclId;
    }

    public Set<NodePermission> getNodePermissions() {
        return nodePermissions;
    }

    public void setNodePermissions(Set<NodePermission> nodePermissions) {
        this.nodePermissions = nodePermissions;
    }

    public boolean hasNodePermission(NodePermission nodePermission) {
        return this.nodePermissions.contains(nodePermission);
    }

    public void addNodePermission(NodePermission nodePermission) {
        this.nodePermissions.add(nodePermission);
    }

    public AbstractUser getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(AbstractUser targetUser) {
        this.targetUser = targetUser;
    }

    public AbstractNode getNode() {
        return node;
    }

    public void setNode(AbstractNode node) {
        this.node = node;
    }
}
