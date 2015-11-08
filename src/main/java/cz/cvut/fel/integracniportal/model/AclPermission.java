package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Permission object.
 * <p>
 * Holds the user's allowed actions, provided to modify the AbstractNode state
 *
 * @author Eldar Iosip
 */
@Entity
@Table(name = "acl_permission")
//, indexes = {
//        @Index(name = "idx", columnList = "user_id, node_id")
//})
public class AclPermission extends AbstractEntity<Long> {

    @Id
    @Column(name = "acl_permission_id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long aclId;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private AbstractUser targetUser;

    @ManyToOne
    @JoinColumn(name = "node_id")
    private AbstractNode node;

    @Column(name = "node_permissions")
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<NodePermission> nodePermissions;

    public AclPermission() {
        this.nodePermissions = new ArrayList<>();
    }

    @Override
    public Long getId() {
        return aclId;
    }

    @Override
    public void setId(Long aclId) {
        this.aclId = aclId;
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
        if (!node.getAclPermissions().containsValue(this)) {
            node.putAclPermission(targetUser.getId(), this);
        }
        this.node = node;
    }

    public List<NodePermission> getNodePermissions() {
        return nodePermissions;
    }

    public void setNodePermissions(List<NodePermission> nodePermissions) {
        this.nodePermissions = nodePermissions;
    }

    public boolean hasNodePermission(NodePermission nodePermission) {
        return this.nodePermissions.contains(nodePermission);
    }

    public void addNodePermission(NodePermission nodePermission) {
        if (!this.hasNodePermission(nodePermission)) {
            this.nodePermissions.add(nodePermission);
        }
    }
}
