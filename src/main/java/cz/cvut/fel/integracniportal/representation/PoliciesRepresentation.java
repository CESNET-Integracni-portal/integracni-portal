package cz.cvut.fel.integracniportal.representation;

import cz.cvut.fel.integracniportal.model.PolicyType;

import java.util.Set;

/**
 * @author Eldar Iosip
 */
public class PoliciesRepresentation {

    private Set<PolicyType> policies;

    public Set<PolicyType> getPolicies() {
        return policies;
    }

    public void setPolicies(Set<PolicyType> policies) {
        this.policies = policies;
    }
}
