package cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects;

import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value(staticConstructor = "of")
public class OrganizationalUnitId {

    private final String id;

    public OrganizationalUnitId(String id) {
        this.id = id;
    }

    public static OrganizationalUnitId of(String id) {
        return new OrganizationalUnitId(id);
    }

}
