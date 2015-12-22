package cz.cvut.fel.integracniportal.model;


import javax.persistence.*;
import java.util.Date;

/**
 * @author Eldar Iosip
 */
@Entity
@Table(name = "resource_node_policy")
public class NodePolicy extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    @Column(name = "policy_id")
    private Long policyId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Policy policy;

    @Column(name = "active_after")
    private Date activeAfter;

    @Column(name = "is_processed")
    private boolean isProcessed;

    @ManyToOne
    @JoinColumn(name = "node", referencedColumnName = "node_id", nullable = false)
    private Node node;

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "user_id", nullable = false)
    private UserDetails owner;

    @Override
    public Long getId() {
        return policyId;
    }

    @Override
    public void setId(Long id) {
        this.policyId = id;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Date getActiveAfter() {
        return activeAfter;
    }

    public void setActiveAfter(Date activeAfter) {
        this.activeAfter = activeAfter;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public UserDetails getOwner() {
        return owner;
    }

    public void setOwner(UserDetails owner) {
        this.owner = owner;
    }
}
