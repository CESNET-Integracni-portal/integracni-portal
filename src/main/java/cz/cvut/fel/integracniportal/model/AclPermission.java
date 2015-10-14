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
    @JoinColumn(name = "target_user", referencedColumnName = "user_id", nullable = false)
    private UserDetails targetUser;

    @ManyToOne
    @JoinColumn(name = "target_user", referencedColumnName = "user_id", nullable = false)
    private Group targetGroup;

    @ManyToOne
    @JoinColumn(name = "node", referencedColumnName = "node_id", nullable = false)
    private AbstractNode node;

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

    public AbstractNode getNode() {
        return node;
    }

    public void setNode(AbstractNode node) {
        this.node = node;
    }
}
