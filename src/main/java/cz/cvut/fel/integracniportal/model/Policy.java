package cz.cvut.fel.integracniportal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import cz.cvut.fel.integracniportal.exceptions.PolicyNotFoundException;

/**
 * @author Eldar Iosip
 */
public enum Policy {

    NOTIFY("NOTIFY"),
    REMOVE("REMOVE");

    private String name;

    Policy(String name) {
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
    public static Policy create(String name) throws PolicyNotFoundException {
        Policy permission;
        try {
            permission = Policy.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new PolicyNotFoundException("policy.notFound", name);
        }
        return permission;
    }
}