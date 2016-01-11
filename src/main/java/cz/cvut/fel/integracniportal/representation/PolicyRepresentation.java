package cz.cvut.fel.integracniportal.representation;

import cz.cvut.fel.integracniportal.model.Policy;
import cz.cvut.fel.integracniportal.model.PolicyType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Eldar Iosip
 */
public class PolicyRepresentation {

    private Long id;

    private Date activeAfter;

    private PolicyType type;

    private boolean isProcessed;

    public PolicyRepresentation(Policy policy) {
        this.id = policy.getId();
        this.activeAfter = policy.getActiveAfter();
        this.type = policy.getType();
        this.isProcessed = policy.isProcessed();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getActiveAfter() {
        return activeAfter;
    }

    public void setActiveAfter(Date activeAfter) {
        this.activeAfter = activeAfter;
    }

    public PolicyType getType() {
        return type;
    }

    public void setType(PolicyType type) {
        this.type = type;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }
}
