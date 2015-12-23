package cz.cvut.fel.integracniportal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import cz.cvut.fel.integracniportal.exceptions.PolicyNotFoundException;

/**
 * @author Eldar Iosip
 */
public enum PolicyType {

    NOTICATION("NOTICATION"),
    AWAITING("AWAITING"),
    AWAITING_REMOVAL("AWAITING_REMOVAL"),
    FAILED_REMOVAL("FAILED_REMOVAL"),
    REMOVE("REMOVE");

    private String name;

    PolicyType(String name) {
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
    public static PolicyType create(String name) throws PolicyNotFoundException {
        PolicyType permission;
        try {
            permission = PolicyType.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new PolicyNotFoundException("policy.notFound", name);
        }
        return permission;
    }
}