package cz.cvut.fel.integracniportal.model;


import javax.persistence.*;
import java.util.Date;

/**
 * @author Eldar Iosip
 */
@Entity
@Table(name = "resource_node_policy")
public class Policy extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    @Column(name = "policy_id")
    private Long policyId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private PolicyType type;

    @Column(name = "active_after")
    private Date activeAfter;

    @Column(name = "attempts")
    private int attempts = 0;

    @Column(name = "is_processed")
    private boolean isProcessed;

    @ManyToOne
    @JoinColumn(name = "node", referencedColumnName = "node_id")
    private Node node;

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "user_id")
    private UserDetails owner;

    @Override
    public Long getId() {
        return policyId;
    }

    @Override
    public void setId(Long id) {
        this.policyId = id;
    }

    public PolicyType getType() {
        return type;
    }

    public void setPolicyType(PolicyType policyType) {
        this.type = policyType;
    }

    public Date getActiveAfter() {
        return activeAfter;
    }

    public void setActiveAfter(Date activeAfter) {
        this.activeAfter = activeAfter;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public void increaseAttempts() {
        this.attempts++;
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
