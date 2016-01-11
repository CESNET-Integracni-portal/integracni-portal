package cz.cvut.fel.integracniportal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import cz.cvut.fel.integracniportal.exceptions.PolicyNotFoundException;

/**
 * @author Eldar Iosip
 */
public enum PolicyState {

    AWAITING("AWAITING"),
    AWAITING_REMOVAL("AWAITING_REMOVAL"),
    AWAITING_NOTIFICATION("AWAITING_NOTIFICATION"),
    FAILED_REMOVAL("FAILED_REMOVAL");

    private String name;

    PolicyState(String name) {
        this.name = name;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public static PolicyState create(String name) throws PolicyNotFoundException {
        PolicyState permission;
        try {
            permission = PolicyState.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new PolicyNotFoundException("policy.state.notFound", name);
        }
        return permission;
    }
}