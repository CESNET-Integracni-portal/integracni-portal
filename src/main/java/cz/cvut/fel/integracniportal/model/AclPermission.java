package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Permission object.
 * <p>
 * It's holding the user's allowed actions,
 * which modify the FSNode state
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
    @Column(name = "acl_id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long aclId;

    @Column(name = "node_permissions")
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<NodePermission> nodePermissions;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AbstractUser targetUser;

    @ManyToOne
    @JoinColumn(name = "node_id")
    private AbstractNode node;

    public AclPermission() {
        this.nodePermissions = new ArrayList<NodePermission>();
    }

    @Override
    public Long getId() {
        return aclId;
    }

    @Override
    public void setId(Long aclId) {
        this.aclId = aclId;
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
        //O(n)
        if (!node.getAcl().containsValue(this)) {
            node.getAcl().put(targetUser.getId(), this);
        }
    }
}
