package cz.cvut.fel.integracniportal.representation;

import cz.cvut.fel.integracniportal.model.PolicyType;

import java.util.Set;

/**
 * @author Eldar Iosip
 */
public class PolicyTypeRepresentation {

    private Set<PolicyType> types;

    public Set<PolicyType> getTypes() {
        return types;
    }

    public void setTypes(Set<PolicyType> types) {
        this.types = types;
    }
}
