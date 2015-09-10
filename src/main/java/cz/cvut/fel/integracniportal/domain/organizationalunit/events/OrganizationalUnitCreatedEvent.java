package cz.cvut.fel.integracniportal.domain.organizationalunit.events;

import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class OrganizationalUnitCreatedEvent {

    private final OrganizationalUnitId id;

    private final String name;

    private final long quotaSize;

}
